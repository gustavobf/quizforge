package com.quizforge;

import com.quizforge.infrastructure.config.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.*;
import org.springframework.boot.test.web.server.LocalServerPort;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
	properties = {"spring.docker.compose.enabled=false",
		"app.cors.allowed-origins=http://localhost:3000,http://127.0.0.1:3001"})
@Import(TestcontainersConfiguration.class)
class QuizforgeApplicationTests {

	@LocalServerPort
	private int port;
	private final HttpClient client = HttpClient.newHttpClient();
	private final JsonMapper json = JsonMapper.builder().build();

	@Test
	void validatesExamLifecycleAgainstPostgres() throws Exception {
		request("GET", "/users/profile", null, null, 401);
		request("GET", "/users/profile", "invalid-token", null, 401);
		JsonNode registration = register();
		String token = registration.path("token").path("accessToken").asText();
		request("GET", "/admin/users", token, null, 403);
		String otherToken = register().path("token").path("accessToken").asText();
		long subjectId = request("POST", "/subjects", token, Map.of("name", "Java"), 201).path("id").asLong();
		assertTrue(subjectId > 0);
		HttpResponse<byte[]> template = client.send(builder("/template", token).GET().build(), HttpResponse.BodyHandlers.ofByteArray());
		assertEquals(200, template.statusCode());
		byte[] workbook = spreadsheet();
		Files.write(Path.of("target", "validation-questions.xlsx"), workbook);
		String boundary = "quizforge-validation-boundary";
		var multipart = new ByteArrayOutputStream();
		multipart.write(("--" + boundary + "\r\nContent-Disposition: form-data; name=\"file\"; filename=\"questions.xlsx\"\r\nContent-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet\r\n\r\n").getBytes(StandardCharsets.UTF_8));
		multipart.write(workbook);
		multipart.write(("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));
		HttpResponse<String> imported = client.send(builder("/questions/import?subjectId=" + subjectId, token)
				.header("Content-Type", "multipart/form-data; boundary=" + boundary)
				.POST(HttpRequest.BodyPublishers.ofByteArray(multipart.toByteArray())).build(), HttpResponse.BodyHandlers.ofString());
		assertEquals(200, imported.statusCode(), imported.body());
		assertEquals(3, json.readTree(imported.body()).path("totalQuestionsImported").asInt());

		JsonNode cancelled = request("POST", "/exams", token, Map.of("title", "Cancelled", "subjectId", subjectId, "quantity", 3), 200);
		long cancelledId = cancelled.path("examId").asLong();
		request("DELETE", "/exams/" + cancelledId, otherToken, null, 400);
		request("DELETE", "/exams/" + cancelledId, token, null, 204);
		request("POST", "/exams/" + cancelledId + "/finish", token, Map.of("answers", Map.of()), 404);
		JsonNode emptySummary = request("GET", "/history/summary", token, null, 200);
		assertEquals(0, emptySummary.path("totalExams").asInt());
		assertEquals(0, emptySummary.path("totalQuestionsAnswered").asInt());
		assertEquals(0, request("GET", "/history/exams", token, null, 200).path("totalElements").asInt());
		assertEquals(0, request("GET", "/history/subjects", token, null, 200).size());

		JsonNode exam = request("POST", "/exams", token, Map.of("title", "Completed", "subjectId", subjectId, "quantity", 3), 200);
		long examId = exam.path("examId").asLong();
		JsonNode first = exam.path("questions").get(0);
		JsonNode second = exam.path("questions").get(1);
		request("POST", "/exams/" + examId + "/finish", token, Map.of("answers", Map.of("999999", List.of(1))), 400);
		request("POST", "/exams/" + examId + "/finish", otherToken, Map.of("answers", Map.of()), 400);
		JsonNode result = request("POST", "/exams/" + examId + "/finish", token, Map.of("answers", Map.of(
				first.path("questionId").asText(), List.of(first.path("alternatives").get(0).path("alternativeId").asLong()),
				second.path("questionId").asText(), List.of(second.path("alternatives").get(1).path("alternativeId").asLong()))), 200);
		assertEquals(1, result.path("correctAnswers").asInt());
		assertEquals(1, result.path("wrongAnswers").asInt());
		assertEquals(1, result.path("unansweredQuestions").asInt());
		assertEquals(100.0 / 3, result.path("score").asDouble(), 0.001);
		request("DELETE", "/exams/" + examId, token, null, 400);
		request("POST", "/exams/" + examId + "/finish", token, Map.of("answers", Map.of()), 400);
		JsonNode detail = request("GET", "/history/exams/" + examId, token, null, 200);
		assertTrue(detail.path("questions").get(0).path("answered").asBoolean());
		assertEquals("Correct", detail.path("questions").get(0).path("yourAnswer").get(0).asText());
		assertFalse(detail.path("questions").get(2).path("answered").asBoolean());
		request("GET", "/history/exams/" + examId, otherToken, null, 404);
		JsonNode summary = request("GET", "/history/summary", token, null, 200);
		assertEquals(1, summary.path("totalExams").asInt());
		assertEquals(2, summary.path("totalQuestionsAnswered").asInt());
		JsonNode stats = request("GET", "/history/subjects", token, null, 200).get(0);
		assertEquals(3, stats.path("totalQuestions").asInt());
		assertEquals(1, stats.path("correctAnswers").asInt());
		String refreshed = request("POST", "/auth/refresh", null,
				Map.of("refreshToken", registration.path("token").path("refreshToken").asText()), 200).path("accessToken").asText();
		request("GET", "/users/profile", refreshed, null, 200);
		request("DELETE", "/users/profile", refreshed, null, 204);
		request("DELETE", "/users/profile", otherToken, null, 204);
	}

	private JsonNode register() throws Exception {
		return request("POST", "/auth/register", null, Map.of("name", "Validation User",
				"email", "validation-" + UUID.randomUUID() + "@example.test", "password", "Validation123!"), 201);
	}

	private HttpRequest.Builder builder(String path, String token) {
		var builder = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
			.header("Origin", "http://127.0.0.1:3001");
		if (token != null) builder.header("Authorization", "Bearer " + token);
		return builder;
	}

	private JsonNode request(String method, String path, String token, Object body, int expectedStatus) throws Exception {
		var payload = body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(json.writeValueAsString(body));
		var response = client.send(builder(path, token).header("Content-Type", "application/json").method(method, payload).build(), HttpResponse.BodyHandlers.ofString());
		assertEquals(expectedStatus, response.statusCode(), method + " " + path + ": " + response.body());
		return response.body().isBlank() ? json.nullNode() : json.readTree(response.body());
	}

	private byte[] spreadsheet() throws Exception {
		try (var workbook = new XSSFWorkbook(); var output = new ByteArrayOutputStream()) {
			var sheet = workbook.createSheet("Questions");
			var header = sheet.createRow(0);
			var columns = List.of("Statement", "Alternative A", "Alternative B", "Correct Answers");
			for (int column = 0; column < columns.size(); column++) header.createCell(column).setCellValue(columns.get(column));
			for (int index = 1; index <= 3; index++) {
				var row = sheet.createRow(index);
				row.createCell(0).setCellValue("Validation question " + index);
				row.createCell(1).setCellValue("Correct");
				row.createCell(2).setCellValue("Wrong");
				row.createCell(3).setCellValue("A");
			}
			workbook.write(output);
			return output.toByteArray();
		}
	}

	@Test
	void contextLoads() {
	}

}
