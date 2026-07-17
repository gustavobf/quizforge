package com.quizforge.adapter.in.web.dto.response;

import lombok.*;

import java.util.*;

@Getter
@Builder
public class HistorySummaryResponse {
    private int totalExams;
    private long totalQuestionsAnswered;
    private double averageScore;
    private double bestScore;
    private double worstScore;
    private List<RecentExamDto> recentExams;

    @Getter
    @Builder
    public static class RecentExamDto {
        private Long id;
        private String title;
        private String subjectName;
        private Double score;
        private Integer correctAnswers;
        private Integer totalQuestions;
        private String finishedAt;
    }
}