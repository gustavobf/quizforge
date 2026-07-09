package com.quizforge.adapter.in.web.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateExamRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private Long subjectId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}