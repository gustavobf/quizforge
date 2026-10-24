package com.quizforge.domain.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class Exam {
    private final Long id;
    private final String title;
    private final Subject subject;
    private final Long ownerId;
    private final List<ExamQuestion> questions;
    private final int totalQuestions;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private Double score;

    private Exam(Long id, String title, Subject subject, Long ownerId, List<ExamQuestion> questions, int totalQuestions,
            LocalDateTime startedAt, LocalDateTime finishedAt, Double score) {
        this.id = id;
        this.title = title;
        this.subject = subject;
        this.ownerId = ownerId;
        this.questions = questions == null ? List.of() : Collections.unmodifiableList(questions);
        this.totalQuestions = totalQuestions;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.score = score;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Subject getSubject() {
        return subject;
    }

    public List<ExamQuestion> getQuestions() {
        return questions == null ? Collections.emptyList() : questions;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public Double getScore() {
        return score;
    }

    public void start() {
        if (startedAt != null) {
            throw new IllegalStateException("Exam already started");
        }
        startedAt = LocalDateTime.now();
    }

    public void finish(double score) {
        if (finishedAt != null) {
            throw new IllegalStateException("Exam already finished");
        }
        if (startedAt == null) {
            throw new IllegalStateException("Exam not started");
        }
        this.finishedAt = LocalDateTime.now();
        this.score = score;
    }

    public boolean isFinished() {
        return finishedAt != null;
    }

    public boolean isStarted() {
        return startedAt != null;
    }

    public static class Builder {
        private Long id;
        private String title;
        private Subject subject;
        private Long ownerId;
        private List<ExamQuestion> questions;
        private int totalQuestions;
        private LocalDateTime startedAt;
        private LocalDateTime finishedAt;
        private Double score;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder subject(Subject subject) {
            this.subject = subject;
            return this;
        }

        public Builder questions(List<ExamQuestion> questions) {
            this.questions = questions;
            return this;
        }

        public Builder ownerId(Long ownerId) {
            this.ownerId = ownerId;
            return this;
        }

        public Builder totalQuestions(int totalQuestions) {
            this.totalQuestions = totalQuestions;
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

        public Builder score(Double score) {
            this.score = score;
            return this;
        }

        public Exam build() {
            return new Exam(id, title, subject, ownerId, questions, totalQuestions, startedAt, finishedAt, score);
        }
    }
}