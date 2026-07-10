package com.quizforge.domain.model;

import lombok.*;

import java.util.*;

@Getter
@Builder
public class ExamHistorySummary {
    private int totalExams;
    private int totalQuestionsAnswered;
    private int totalCorrectAnswers;
    private double averageScore;
    private int bestScore;
    private int worstScore;
    private List<ExamHistory> recentExams;
}