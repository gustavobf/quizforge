package com.quizforge.domain.exception;

public class ExamNotInProgressException extends ExamException {

    private final Long examId;

    public ExamNotInProgressException (Long examId) {
        super("Exam is not in progress: " + examId, "EXAM_NOT_IN_PROGRESS");
        this.examId = examId;
    }

    public Long getExamId () {
        return examId;
    }
}