package com.quizforge.adapter.in.web.dto.response;

import lombok.*;

import java.time.*;
import java.util.*;

@Getter
@Builder
public class ExamResultResponse {
    private Long examId;
    private String title;
    private String subjectName;
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Integer wrongAnswers;
    private Integer unansweredQuestions;
    private Double score;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private Integer timeSpentInMinutes;
    private List<QuestionResultDto> questions;

    @Getter
    @Builder
    public static class QuestionResultDto {
        private Integer number;
        private String statement;
        private List<String> yourAnswer;
        private List<String> correctAnswer;
        private Boolean isCorrect;
        private Boolean answered;
        private String questionType;
    }
}