package com.quizforge.adapter.in.web.dto.response;

import com.quizforge.domain.enumtype.*;
import lombok.*;

import java.time.*;
import java.util.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamResultResponse {

    private Long examId;
    private String title;
    private String subjectName;
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Integer wrongAnswers;
    private Double score;
    private ExamStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private Integer timeSpentInMinutes;
    private List<QuestionResultDto> questions;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionResultDto {
        private Integer number;
        private String statement;
        private String yourAnswer;
        private String correctAnswer;
        private Boolean isCorrect;
    }
}