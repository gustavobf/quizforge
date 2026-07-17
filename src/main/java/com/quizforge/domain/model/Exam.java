package com.quizforge.domain.model;

import lombok.*;

import java.time.*;
import java.util.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Exam {

    private Long id;
    private String title;
    private Subject subject;
    private List<ExamQuestion> questions;
    private int totalQuestions;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private Double score;

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

    public List<ExamQuestion> getQuestions() {
        return questions == null
                ? Collections.emptyList()
                : questions;
    }
}