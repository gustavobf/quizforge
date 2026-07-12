package com.quizforge.adapter.in.web.dto.response;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateExamAlternativeResponse {
    private Long alternativeId;
    private String description;
}