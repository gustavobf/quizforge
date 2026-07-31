package com.quizforge.domain.model;

import java.time.LocalDateTime;

public record ExamHistory(
        Long id,
        String title,
        String subjectName,
        int totalQuestions,
        int correctAnswers,
        int wrongAnswers,
        double score,
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        int timeSpentInMinutes
) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
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

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder subjectName(String subjectName) {
            this.subjectName = subjectName;
            return this;
        }

        public Builder totalQuestions(int totalQuestions) {
            this.totalQuestions = totalQuestions;
            return this;
        }

        public Builder correctAnswers(int correctAnswers) {
            this.correctAnswers = correctAnswers;
            return this;
        }

        public Builder wrongAnswers(int wrongAnswers) {
            this.wrongAnswers = wrongAnswers;
            return this;
        }

        public Builder score(double score) {
            this.score = score;
            return this;
        }

        public Builder startedAt(LocalDateTime startedAt) {
            this.startedAt = startedAt;
            return this;
        }

        public Builder finishedAt(LocalDateTime finishedAt) {
            this.finishedAt = finishedAt;
            return this;
        }

        public Builder timeSpentInMinutes(int timeSpentInMinutes) {
            this.timeSpentInMinutes = timeSpentInMinutes;
            return this;
        }

        public ExamHistory build() {
            return new ExamHistory(id, title, subjectName, totalQuestions, correctAnswers, wrongAnswers, score, startedAt, finishedAt, timeSpentInMinutes);
        }
    }
}