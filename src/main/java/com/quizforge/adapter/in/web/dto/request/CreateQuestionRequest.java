package com.quizforge.adapter.in.web.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateQuestionRequest(

        @NotBlank(message = "Question statement is required") String statement,

        @NotEmpty(message = "Question must contain at least one alternative") List<@Valid CreateAlternativeRequest> alternatives

) {
}