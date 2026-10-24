package com.quizforge.domain.exception;

public class QuestionAlreadyAnsweredException extends QuestionException {

    private final Long questionId;

    public QuestionAlreadyAnsweredException (Long questionId) {
        super("Question already answered: " + questionId, "QUESTION_ALREADY_ANSWERED");
        this.questionId = questionId;
    }

    public Long getQuestionId () {
        return questionId;
    }
}