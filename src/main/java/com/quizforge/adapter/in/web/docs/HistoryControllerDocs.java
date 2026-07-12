package com.quizforge.adapter.in.web.docs;

import com.quizforge.adapter.in.web.dto.response.*;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.*;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Tag(name = "History", description = "Endpoints for exam history and statistics")
public interface HistoryControllerDocs {

    @Operation(summary = "Get history summary", description = "Returns a summary of all completed exams including total count, average score, and recent exams")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Summary retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HistorySummaryResponse.class), examples = @ExampleObject(value = """
                    {
                      "totalExams": 15,
                      "totalQuestionsAnswered": 150,
                      "averageScore": 72.5,
                      "bestScore": 95.0,
                      "worstScore": 45.0,
                      "recentExams": [
                        {
                          "id": 1,
                          "title": "Java Basic Exam",
                          "subjectName": "Java",
                          "score": 80.0,
                          "correctAnswers": 8,
                          "totalQuestions": 10,
                          "finishedAt": "2026-07-10T10:45:00"
                        }
                      ]
                    }
                    """))), @ApiResponse(responseCode = "500", description = "Internal server error")})
    ResponseEntity<HistorySummaryResponse> getSummary ();

    @Operation(summary = "Get all completed exams", description = "Returns a paginated list of all completed exams with their results")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exams retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class), examples = @ExampleObject(value = """
                    {
                      "content": [
                        {
                          "examId": 1,
                          "title": "Java Basic Exam",
                          "subjectName": "Java",
                          "totalQuestions": 10,
                          "correctAnswers": 8,
                          "wrongAnswers": 2,
                          "score": 80.0,
                          "startedAt": "2026-07-10T10:30:00",
                          "finishedAt": "2026-07-10T10:45:00",
                          "timeSpentInMinutes": 15
                        }
                      ],
                      "pageable": {
                        "pageNumber": 0,
                        "pageSize": 10,
                        "totalPages": 2,
                        "totalElements": 15
                      }
                    }
                    """))), @ApiResponse(responseCode = "500", description = "Internal server error")})
    ResponseEntity<Page<HistoryExamResponse>> getExams (
            @Parameter(description = "Page number (0-indexed)", example = "0") @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of items per page (max 100)", example = "10") @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Field to sort by", example = "finishedAt") @RequestParam(defaultValue = "finishedAt") String sortBy,

            @Parameter(description = "Sort direction (asc or desc)", example = "desc") @RequestParam(defaultValue = "desc") String sortDirection);

    @Operation(summary = "Get exam detail", description = "Returns detailed information about a specific completed exam including all questions and answers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exam detail retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HistoryExamResponse.class), examples = @ExampleObject(value = """
                    {
                      "examId": 1,
                      "title": "Java Basic Exam",
                      "subjectName": "Java",
                      "totalQuestions": 10,
                      "correctAnswers": 8,
                      "wrongAnswers": 2,
                      "score": 80.0,
                      "startedAt": "2026-07-10T10:30:00",
                      "finishedAt": "2026-07-10T10:45:00",
                      "timeSpentInMinutes": 15,
                      "questions": [
                        {
                          "number": 1,
                          "questionId": 101,
                          "statement": "What is the default value of a boolean in Java?",
                          "alternatives": [
                            {
                              "alternativeId": 1,
                              "description": "true",
                              "isCorrect": false
                            },
                            {
                              "alternativeId": 2,
                              "description": "false",
                              "isCorrect": true
                            }
                          ],
                          "yourAnswer": ["false"],
                          "correctAnswer": ["false"],
                          "isCorrect": true,
                          "questionType": "SINGLE_CHOICE"
                        }
                      ]
                    }
                    """))), @ApiResponse(responseCode = "404", description = "Exam not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    ResponseEntity<HistoryExamResponse> getExamDetail (
            @Parameter(description = "Exam ID", required = true, example = "1") @PathVariable Long examId);

    @Operation(summary = "Get subject statistics", description = "Returns statistics grouped by subject including total exams, questions, and scores")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subject statistics retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SubjectStatsResponse.class), examples = @ExampleObject(value = """
                    [
                      {
                        "subjectId": 1,
                        "subjectName": "Java",
                        "totalExams": 5,
                        "totalQuestions": 50,
                        "correctAnswers": 40,
                        "averageScore": 80.0,
                        "bestScore": 95.0
                      },
                      {
                        "subjectId": 2,
                        "subjectName": "Python",
                        "totalExams": 3,
                        "totalQuestions": 30,
                        "correctAnswers": 21,
                        "averageScore": 70.0,
                        "bestScore": 85.0
                      }
                    ]
                    """))), @ApiResponse(responseCode = "500", description = "Internal server error")})
    ResponseEntity<List<SubjectStatsResponse>> getSubjectStats ();
}