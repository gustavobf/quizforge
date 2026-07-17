package com.quizforge.adapter.in.web.dto.response;

import lombok.*;

import java.time.*;
import java.util.*;

@Getter
@Builder
public class HistoryExamResponse {
    private Long examId;
    private String title;
    private String subjectName;
    private Integer totalQuestions;
    private Double score;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private Integer timeSpentInMinutes;
    private List<QuestionDetailDto> questions;

    @Getter
    @Builder
    public static class QuestionDetailDto {
        private Integer number;
        private Long questionId;
        private String statement;
        private List<AlternativeDto> alternatives;
        private List<String> yourAnswer;
        private List<String> correctAnswer;
        private Boolean isCorrect;
        private String questionType;
    }

    @Getter
    @Builder
    public static class AlternativeDto {
        private Long alternativeId;
        private String description;
        private Boolean isCorrect;
    }
}