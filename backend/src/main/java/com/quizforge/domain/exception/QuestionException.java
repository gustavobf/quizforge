package com.quizforge.domain.exception;

public class QuestionException extends RuntimeException {

    private final String errorCode;

    public QuestionException (String message) {
        super(message);
        this.errorCode = "QUESTION_ERROR";
    }

    public QuestionException (String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public QuestionException (String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "QUESTION_ERROR";
    }

    public String getErrorCode () {
        return errorCode;
    }
}