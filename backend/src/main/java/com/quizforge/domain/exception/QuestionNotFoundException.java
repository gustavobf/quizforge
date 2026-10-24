package com.quizforge.domain.exception;

import lombok.*;

@Getter
public class QuestionNotFoundException extends RuntimeException {

    private final Long questionId;

    public QuestionNotFoundException (Long questionId) {
        super("Question not found with id: " + questionId);
        this.questionId = questionId;
    }

    public QuestionNotFoundException (String message, Long questionId) {
        super(message);
        this.questionId = questionId;
    }

    public Long getQuestionId () {
        return questionId;
    }
}