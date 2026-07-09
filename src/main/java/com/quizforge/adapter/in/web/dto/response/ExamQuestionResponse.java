package com.quizforge.adapter.in.web.dto.response;

import lombok.*;

import java.util.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamQuestionResponse {

    private Long examId;
    private Integer totalQuestions;
    private Integer currentQuestionNumber;
    private String statement;
    private String subjectName;
    private List<AlternativeDto> alternatives;
    private Boolean isAnswered;
    private Long selectedAlternativeId;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlternativeDto {
        private Long id;
        private String statement;
    }
}