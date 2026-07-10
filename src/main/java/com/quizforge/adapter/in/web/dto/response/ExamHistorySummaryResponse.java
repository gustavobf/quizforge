package com.quizforge.adapter.in.web.dto.response;

import lombok.*;

import java.util.*;

@Getter
@Builder
public class ExamHistorySummaryResponse {
    private int totalExams;
    private int totalQuestionsAnswered;
    private int totalCorrectAnswers;
    private double averageScore;
    private int bestScore;
    private int worstScore;
    private List<ExamHistoryResponse> recentExams;
}