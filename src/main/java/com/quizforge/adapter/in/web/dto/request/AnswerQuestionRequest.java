package com.quizforge.adapter.in.web.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnswerQuestionRequest {

    @NotNull(message = "Alternative ID is required")
    private Long alternativeId;
}