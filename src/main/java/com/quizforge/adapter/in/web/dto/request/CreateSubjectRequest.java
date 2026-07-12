package com.quizforge.adapter.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request data for creating a new subject")
public class CreateSubjectRequest {

    @NotBlank(message = "Subject name is required")
    @Size(min = 3, max = 100, message = "Subject name must be between 3 and 100 characters")
    @Schema(description = "Subject name", example = "JavaScript", required = true)
    private String name;

    @Size(max = 500, message = "Description must be less than 500 characters")
    @Schema(description = "Subject description", example = "JavaScript programming language - ES6, React, Node.js", required = false)
    private String description;
}