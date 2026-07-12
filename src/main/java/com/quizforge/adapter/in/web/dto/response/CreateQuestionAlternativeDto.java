package com.quizforge.adapter.in.web.dto.response;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateQuestionAlternativeDto {
    private Long id;
    private String statement;
    private boolean correct;
}
