package com.quizforge.domain.model;

import com.quizforge.domain.enumtype.*;
import com.quizforge.domain.exception.*;
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
    private String description;
    private Subject subject;
    private List<ExamQuestion> questions;
    private int totalQuestions;
    private int currentQuestionIndex;
    private ExamStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private Long userId;

    public void start () {
        if (status != ExamStatus.NOT_STARTED) {
            throw new BusinessException("Exam already started");
        }
        this.status = ExamStatus.IN_PROGRESS;
        this.startedAt = LocalDateTime.now();
        this.currentQuestionIndex = 0;
    }

    public ExamQuestion getCurrentQuestion () {
        if (questions == null || currentQuestionIndex >= questions.size()) {
            return null;
        }
        return questions.get(currentQuestionIndex);
    }

    public void nextQuestion () {
        if (currentQuestionIndex < questions.size() - 1) {
            currentQuestionIndex++;
        }
    }

    public void previousQuestion () {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
        }
    }

    public void goToQuestion (int index) {
        if (index >= 0 && index < questions.size()) {
            this.currentQuestionIndex = index;
        }
    }

    public boolean isFinished () {
        return status == ExamStatus.COMPLETED;
    }

    public void finish () {
        if (status == ExamStatus.COMPLETED) {
            throw new BusinessException("Exam already completed");
        }
        this.status = ExamStatus.COMPLETED;
        this.finishedAt = LocalDateTime.now();
    }

    public int getAnsweredQuestionsCount () {
        return (int) questions.stream().filter(ExamQuestion::isAnswered).count();
    }

    public boolean isComplete () {
        return questions.stream().allMatch(ExamQuestion::isAnswered);
    }

    public int getCorrectAnswersCount () {
        return (int) questions.stream().filter(ExamQuestion::isCorrect).count();
    }

    public int getWrongAnswersCount () {
        return getAnsweredQuestionsCount() - getCorrectAnswersCount();
    }

    public double getScore () {
        if (totalQuestions == 0)
            return 0;
        return (getCorrectAnswersCount() * 100.0) / totalQuestions;
    }
}