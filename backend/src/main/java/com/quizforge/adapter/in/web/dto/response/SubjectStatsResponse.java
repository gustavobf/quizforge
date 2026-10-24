package com.quizforge.adapter.in.web.dto.response;

import lombok.*;

@Getter
@Builder
public class SubjectStatsResponse {
    private Long subjectId;
    private String subjectName;
    private Integer totalExams;
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Double averageScore;
    private Double bestScore;
}