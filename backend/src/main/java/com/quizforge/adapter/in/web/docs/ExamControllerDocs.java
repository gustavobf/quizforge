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
            @ApiResponse(responseCode = "200", description = "Exam created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreateExamResponse.class), examples = @ExampleObject(value = """
                    {
                      "examId": 1,
                      "title": "Java Basic Exam",
                      "totalQuestions": 10,
                      "subjectName": "Java",
                      "status": "NOT_STARTED",
                      "questions": [
                        {
                          "questionId": 101,
                          "statement": "What is the capital of Brazil?",
                          "alternatives": [
                            {
                              "alternativeId": 1,
                              "description": "Brasília"
                            },
                            {
                              "alternativeId": 2,
                              "description": "Rio de Janeiro"
                            },
                            {
                              "alternativeId": 3,
                              "description": "São Paulo"
                            },
                            {
                              "alternativeId": 4,
                              "description": "Salvador"
                            }
                          ],
                          "orderNumber": 1
                        }
                      ]
                    }
                    """))), @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)})
    ResponseEntity<CreateExamResponse> createExam (
            @Valid @RequestBody @Parameter(description = "Exam creation data", required = true, example = """
                    {
                      "title": "Java Basic Exam",
                      "subjectId": 5,
                      "quantity": 10
                    }
                    """) CreateExamRequest request);

    @Operation(summary = "Finish exam", description = "Completes the exam and returns the results with the answer key")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exam finished successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExamResultResponse.class), examples = @ExampleObject(value = """
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
                          "statement": "What is the capital of Brazil?",
                          "yourAnswer": "Brasília",
                          "correctAnswer": "Brasília",
                          "isCorrect": true,
                          "questionType": "SINGLE_CHOICE"
                        },
                        {
                          "number": 2,
                          "statement": "Which of the following are Java keywords?",
                          "yourAnswer": "static, public",
                          "correctAnswer": "static, public, class",
                          "isCorrect": false,
                          "questionType": "MULTIPLE_CHOICE"
                        }
                      ]
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "Cannot finish exam - Exam already finished or not started", content = @Content),
            @ApiResponse(responseCode = "404", description = "Exam not found", content = @Content)})
    ResponseEntity<ExamResultResponse> finishExam (
            @PathVariable @Parameter(description = "Exam ID", required = true, example = "1") Long examId,
            @RequestBody @Parameter(description = "User answers map where key is questionId and value is list of selected alternativeIds", required = true, example = """
                    {
                      "answers": {
                        "101": [1],
                        "102": [2, 4],
                        "103": []
                      }
                    }
                    """) FinishExamRequest request);
}