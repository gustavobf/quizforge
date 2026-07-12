package com.quizforge.adapter.in.web.dto.response;

import lombok.*;

import java.util.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateQuestionResponse {
    private Long id;
    private Long subjectId;
    private String subjectName;
    private String statement;
    private List<CreateQuestionAlternativeDto> alternatives;
    private Integer orderNumber;
}