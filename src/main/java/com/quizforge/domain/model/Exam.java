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

    public void start () {
        if (this.startedAt != null) {
            throw new IllegalStateException("Exam already started");
        }
        this.startedAt = LocalDateTime.now();
    }

    public void finish () {
        if (this.finishedAt != null) {
            throw new IllegalStateException("Exam already finished");
        }
        if (this.startedAt == null) {
            throw new IllegalStateException("Exam not started");
        }
        this.finishedAt = LocalDateTime.now();
        this.score = calculateScore();
    }

    public int getCorrectAnswersCount () {
        if (questions == null)
            return 0;
        return (int) questions.stream().filter(ExamQuestion::isAnswered).filter(ExamQuestion::isCorrect).count();
    }

    public int getWrongAnswersCount () {
        if (questions == null)
            return 0;
        return (int) questions.stream().filter(ExamQuestion::isAnswered).filter(q -> !q.isCorrect()).count();
    }

    public Double calculateScore () {
        if (totalQuestions == 0)
            return 0.0;
        return (getCorrectAnswersCount() * 100.0) / totalQuestions;
    }

    public Double getScore () {
        if (score != null) {
            return score;
        }
        return calculateScore();
    }

    public List<ExamQuestion> getQuestions () {
        if (questions == null) {
            return new ArrayList<>();
        }
        return questions;
    }
}