package com.quizforge.adapter.in.web.docs;

import com.quizforge.adapter.in.web.dto.request.*;
import com.quizforge.adapter.in.web.dto.response.*;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.*;
import jakarta.validation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Exams", description = "Endpoints for exam management")
public interface ExamControllerDocs {

    @Operation(summary = "Create a new exam", description = "Creates an exam with a specific number of random questions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exam created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExamResponse.class), examples = @ExampleObject(value = """
                    {
                      "examId": 1,
                      "title": "Java Basic Exam",
                      "totalQuestions": 10,
                      "status": "NOT_STARTED"
                    }
                    """))), @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)})
    ResponseEntity<ExamResponse> createExam (
            @Valid @RequestBody @Parameter(description = "Exam creation data", required = true) CreateExamRequest request);

    @Operation(summary = "Get current question", description = "Returns the current exam question without the correct answer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Question found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExamQuestionResponse.class), examples = @ExampleObject(value = """
                    {
                      "examId": 1,
                      "totalQuestions": 10,
                      "currentQuestionNumber": 1,
                      "statement": "What is the capital of Brazil?",
                      "alternatives": [
                        { "id": 1, "statement": "São Paulo" },
                        { "id": 2, "statement": "Rio de Janeiro" },
                        { "id": 3, "statement": "Brasília" },
                        { "id": 4, "statement": "Salvador" }
                      ],
                      "isAnswered": false,
                      "selectedAlternativeId": null
                    }
                    """))), @ApiResponse(responseCode = "404", description = "Exam not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Exam is not in progress", content = @Content)})
    ResponseEntity<ExamQuestionResponse> getCurrentQuestion (
            @PathVariable @Parameter(description = "Exam ID", required = true, example = "1") Long examId);

    @Operation(summary = "Answer current question", description = "Registers the user's answer for the current question")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Answer registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or question already answered", content = @Content),
            @ApiResponse(responseCode = "404", description = "Exam not found", content = @Content)})
    ResponseEntity<Void> answerQuestion (
            @PathVariable @Parameter(description = "Exam ID", required = true, example = "1") Long examId,
            @Valid @RequestBody @Parameter(description = "Answer data", required = true) AnswerQuestionRequest request);

    @Operation(summary = "Finish exam", description = "Completes the exam and returns the results with the answer key")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exam finished successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExamResultResponse.class), examples = @ExampleObject(value = """
                    {
                      "examId": 1,
                      "title": "Java Basic Exam",
                      "totalQuestions": 10,
                      "correctAnswers": 8,
                      "wrongAnswers": 2,
                      "score": 80.0,
                      "status": "COMPLETED",
                      "startedAt": "2026-07-09T10:30:00",
                      "finishedAt": "2026-07-09T10:45:00",
                      "timeSpentInMinutes": 15,
                      "questions": [
                        {
                          "number": 1,
                          "statement": "What is the capital of Brazil?",
                          "yourAnswer": "Brasília",
                          "correctAnswer": "Brasília",
                          "isCorrect": true
                        }
                      ]
                    }
                    """))), @ApiResponse(responseCode = "400", description = "Cannot finish exam", content = @Content),
            @ApiResponse(responseCode = "404", description = "Exam not found", content = @Content)})
    ResponseEntity<ExamResultResponse> finishExam (
            @PathVariable @Parameter(description = "Exam ID", required = true, example = "1") Long examId);
}