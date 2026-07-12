package com.quizforge.adapter.in.web.dto.response;

import lombok.*;

import java.time.*;

@Getter
@Builder
public class ExamHistoryResponse {
    private Long id;
    private String title;
    private String subjectName;
    private int totalQuestions;
    private int correctAnswers;
    private int wrongAnswers;
    private double score;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private int timeSpentInMinutes;
}