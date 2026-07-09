package com.quizforge.domain.exception;

public class ExamAlreadyCompletedException extends ExamException {

    private final Long examId;

    public ExamAlreadyCompletedException (Long examId) {
        super("Exam already completed: " + examId, "EXAM_ALREADY_COMPLETED");
        this.examId = examId;
    }

    public Long getExamId () {
        return examId;
    }
}