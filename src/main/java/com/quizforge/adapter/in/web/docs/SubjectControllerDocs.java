package com.quizforge.adapter.in.web.docs;

import com.quizforge.adapter.in.web.dto.request.*;
import com.quizforge.adapter.in.web.dto.response.*;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Tag(name = "Subjects", description = "Endpoints for subject management")
public interface SubjectControllerDocs {

    @Operation(summary = "Get all subjects", description = "Returns a list of all available subjects")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Subjects retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @GetMapping
    ResponseEntity<List<SubjectResponse>> getAllSubjects ();

    @Operation(summary = "Get subject by ID", description = "Returns a subject by its ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Subject retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Subject not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @GetMapping("/{id}")
    ResponseEntity<SubjectResponse> getSubjectById (@PathVariable Long id);

    @Operation(summary = "Create a new subject", description = "Creates a new subject with the provided data")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Subject created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @PostMapping
    ResponseEntity<SubjectResponse> createSubject (@RequestBody CreateSubjectRequest request);

    @Operation(summary = "Update a subject", description = "Updates an existing subject")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Subject updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Subject not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @PutMapping("/{id}")
    ResponseEntity<SubjectResponse> updateSubject (@PathVariable Long id, @RequestBody CreateSubjectRequest request);

    @Operation(summary = "Delete a subject", description = "Deletes a subject by its ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Subject deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Subject not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteSubject (@PathVariable Long id);
}