package com.quizforge.domain.exception;

public class ExamNotCompleteException extends ExamException {

    private final Long examId;
    private final int answered;
    private final int total;

    public ExamNotCompleteException (Long examId, int answered, int total) {
        super("Cannot finish exam: not all questions answered. Answered: " + answered + "/" + total,
                "EXAM_NOT_COMPLETE");
        this.examId = examId;
        this.answered = answered;
        this.total = total;
    }

    public Long getExamId () {
        return examId;
    }

    public int getAnswered () {
        return answered;
    }

    public int getTotal () {
        return total;
    }
}