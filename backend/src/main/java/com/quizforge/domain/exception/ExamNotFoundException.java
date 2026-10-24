package com.quizforge.domain.exception;

public class ExamNotFoundException extends RuntimeException {

    private final Long examId;

    public ExamNotFoundException (Long examId) {
        super("Exam not found with id: " + examId);
        this.examId = examId;
    }

    public ExamNotFoundException (String message, Long examId) {
        super(message);
        this.examId = examId;
    }

    public Long getExamId () {
        return examId;
    }
}