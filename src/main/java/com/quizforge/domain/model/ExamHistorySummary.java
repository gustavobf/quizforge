package com.quizforge.domain.model;

import java.util.Collections;
import java.util.List;

public record ExamHistorySummary(
        int totalExams,
        int totalQuestionsAnswered,
        int totalCorrectAnswers,
        double averageScore,
        int bestScore,
        int worstScore,
        List<ExamHistory> recentExams
) {

    public ExamHistorySummary {
        recentExams = recentExams == null ? List.of() : Collections.unmodifiableList(recentExams);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int totalExams;
        private int totalQuestionsAnswered;
        private int totalCorrectAnswers;
        private double averageScore;
        private int bestScore;
        private int worstScore;
        private List<ExamHistory> recentExams;

        public Builder totalExams(int totalExams) {
            this.totalExams = totalExams;
            return this;
        }

        public Builder totalQuestionsAnswered(int totalQuestionsAnswered) {
            this.totalQuestionsAnswered = totalQuestionsAnswered;
            return this;
        }

        public Builder totalCorrectAnswers(int totalCorrectAnswers) {
            this.totalCorrectAnswers = totalCorrectAnswers;
            return this;
        }

        public Builder averageScore(double averageScore) {
            this.averageScore = averageScore;
            return this;
        }

        public Builder bestScore(int bestScore) {
            this.bestScore = bestScore;
            return this;
        }

        public Builder worstScore(int worstScore) {
            this.worstScore = worstScore;
            return this;
        }

        public Builder recentExams(List<ExamHistory> recentExams) {
            this.recentExams = recentExams;
            return this;
        }

        public ExamHistorySummary build() {
            return new ExamHistorySummary(totalExams, totalQuestionsAnswered, totalCorrectAnswers, averageScore, bestScore, worstScore, recentExams);
        }
    }
}