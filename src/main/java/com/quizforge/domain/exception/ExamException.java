package com.quizforge.domain.exception;

public class ExamException extends RuntimeException {

    private final String errorCode;

    public ExamException (String message) {
        super(message);
        this.errorCode = "EXAM_ERROR";
    }

    public ExamException (String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ExamException (String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "EXAM_ERROR";
    }

    public String getErrorCode () {
        return errorCode;
    }
}