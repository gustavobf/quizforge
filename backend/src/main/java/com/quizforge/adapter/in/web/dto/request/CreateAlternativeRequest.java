package com.quizforge.adapter.in.web.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAlternativeRequest {

    @NotBlank(message = "Alternative description is required")
    private String description;

    @NotNull(message = "Correct flag is required")
    private Boolean correct;
}