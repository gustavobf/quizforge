package com.quizforge.adapter.in.web.dto.request;

import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateQuestionRequest {

    @NotBlank(message = "Statement is required")
    private String statement;

    @NotNull(message = "Subject ID is required")
    private Long subjectId;

    @NotEmpty(message = "Alternatives are required")
    @Valid
    private List<CreateAlternativeRequest> alternatives;
}