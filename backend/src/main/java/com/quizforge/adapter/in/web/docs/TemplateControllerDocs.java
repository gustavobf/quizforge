package com.quizforge.adapter.in.web.docs;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.*;
import org.springframework.http.*;

@Tag(name = "Template", description = "Endpoints for template downloads")
public interface TemplateControllerDocs {

    @Operation(summary = "Download import template", description = "Downloads the Excel template file for question import")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Template downloaded successfully", content = @Content(mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")),
            @ApiResponse(responseCode = "500", description = "Error generating template", content = @Content)})
    ResponseEntity<byte[]> downloadTemplate ();
}