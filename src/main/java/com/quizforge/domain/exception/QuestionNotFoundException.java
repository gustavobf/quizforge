package com.quizforge.domain.exception;

public class QuestionNotFoundException extends BusinessException {

    public QuestionNotFoundException (Long id) {
        super("Question with id " + id + " not found");
    }
}