package com.quizforge.adapter.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateAlternativeRequest(

        @NotBlank(message = "Alternative description is required") String description,

        boolean correct

) {
}
