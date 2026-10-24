package com.quizforge.adapter.in.web.dto.request;

import lombok.*;

import java.util.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportQuestionRow {
    private String statement;
    private Long subjectId;
    private Map<String, String> alternatives;
    private Set<String> correctAnswers;
}