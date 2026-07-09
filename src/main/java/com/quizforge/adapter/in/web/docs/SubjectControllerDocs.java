package com.quizforge.adapter.in.web.docs;

import com.quizforge.adapter.in.web.dto.response.*;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.*;
import org.springframework.http.*;

import java.util.*;

@Tag(name = "Subjects", description = "Endpoints for subject management")
public interface SubjectControllerDocs {

    @Operation(summary = "Get all subjects", description = "Returns a list of all available subjects")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subjects retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SubjectResponse.class), examples = @ExampleObject(value = """
                    [
                        {
                            "id": 1,
                            "name": "Java",
                            "description": "Java programming language"
                        },
                        {
                            "id": 2,
                            "name": "Python",
                            "description": "Python programming language"
                        }
                    ]
                    """))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)})
    ResponseEntity<List<SubjectResponse>> getAllSubjects ();
}