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
import org.springframework.web.multipart.*;

@Tag(name = "Questions", description = "Endpoints for question management")
public interface QuestionControllerDocs {

    @Operation(summary = "Create a new question", description = "Creates a new question with its alternatives")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Question created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = QuestionResponse.class), examples = @ExampleObject(value = """
                    {
                      "id": 1,
                      "statement": "What is the capital of Brazil?",
                      "type": "SINGLE_CHOICE",
                      "subjectId": 1,
                      "subjectName": "Geography",
                      "alternatives": [
                        { "id": 1, "statement": "São Paulo", "correct": false },
                        { "id": 2, "statement": "Rio de Janeiro", "correct": false },
                        { "id": 3, "statement": "Brasília", "correct": true },
                        { "id": 4, "statement": "Salvador", "correct": false }
                      ]
                    }
                    """))), @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Subject not found", content = @Content)})
    ResponseEntity<QuestionResponse> create (
            @Valid @RequestBody @Parameter(description = "Question data", required = true) CreateQuestionRequest request);

    @Operation(summary = "Import questions from spreadsheet", description = "Imports questions from an Excel file (.xlsx) for a specific subject")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Import completed successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ImportQuestionsResponse.class), examples = @ExampleObject(value = """
                    {
                      "importedCount": 116
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "Invalid file or format", content = @Content),
            @ApiResponse(responseCode = "404", description = "Subject not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal error processing file", content = @Content)})
    ResponseEntity<ImportQuestionsResponse> importQuestions (
            @RequestParam("file") @Parameter(description = "Excel file with questions", required = true) MultipartFile file,

            @RequestParam(value = "subjectId") @Parameter(description = "Subject ID for all imported questions", required = true, example = "1") Long subjectId);
}