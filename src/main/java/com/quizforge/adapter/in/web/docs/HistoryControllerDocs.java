package com.quizforge.adapter.in.web.docs;

import com.quizforge.adapter.in.web.dto.response.*;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Tag(name = "History", description = "Endpoints for exam history management")
public interface HistoryControllerDocs {

    @Operation(summary = "Get exam history", description = "Returns paginated list of completed exams for the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "History retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExamHistoryResponse.class), examples = @ExampleObject(value = """
                    [
                      {
                        "id": 1,
                        "title": "Java Basic Exam",
                        "subjectName": "Java",
                        "totalQuestions": 10,
                        "correctAnswers": 8,
                        "wrongAnswers": 2,
                        "score": 80.0,
                        "status": "COMPLETED",
                        "startedAt": "2026-07-09T10:30:00",
                        "finishedAt": "2026-07-09T10:45:00",
                        "timeSpentInMinutes": 15
                      }
                    ]
                    """))), @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)})
    ResponseEntity<List<ExamHistoryResponse>> getHistory (
            @Parameter(description = "Page number (0-indexed)", example = "0") @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of items per page", example = "20") @RequestParam(defaultValue = "20") int size);

    @Operation(summary = "Get exam history summary", description = "Returns summary statistics of all completed exams")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Summary retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExamHistorySummaryResponse.class), examples = @ExampleObject(value = """
                    {
                      "totalExams": 5,
                      "totalQuestionsAnswered": 50,
                      "totalCorrectAnswers": 38,
                      "averageScore": 76.0,
                      "bestScore": 90,
                      "worstScore": 60,
                      "recentExams": [...]
                    }
                    """))), @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)})
    ResponseEntity<ExamHistorySummaryResponse> getSummary ();

    @Operation(summary = "Get history by subject", description = "Returns completed exams filtered by subject")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "History retrieved successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Subject not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)})
    ResponseEntity<List<ExamHistoryResponse>> getBySubject (
            @Parameter(description = "Subject ID", required = true, example = "1") @PathVariable Long subjectId);

    @Operation(summary = "Get exam details by ID", description = "Returns detailed information of a specific completed exam")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exam found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExamHistoryResponse.class))),
            @ApiResponse(responseCode = "404", description = "Exam not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)})
    ResponseEntity<ExamHistoryResponse> getById (
            @Parameter(description = "Exam ID", required = true, example = "1") @PathVariable Long examId);
}