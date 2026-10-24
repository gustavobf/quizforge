package com.quizforge.adapter.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateExamRequest {

    @NotBlank(message = "Title is required")
    @Schema(description = "Exam title", example = "Java Basic Exam")
    private String title;

    @Schema(description = "Subject ID (optional)", example = "5", nullable = true)
    private Long subjectId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 100, message = "Quantity must be at most 100")
    @Schema(description = "Number of questions", example = "10", minimum = "1", maximum = "100")
    private Integer quantity;
}