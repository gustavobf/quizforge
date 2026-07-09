package com.quizforge.domain.exception;

public class InvalidAnswerException extends QuestionException {

    private final Long questionId;
    private final Long alternativeId;

    public InvalidAnswerException (Long questionId, Long alternativeId) {
        super("Invalid answer for question " + questionId + ": alternative " + alternativeId, "INVALID_ANSWER");
        this.questionId = questionId;
        this.alternativeId = alternativeId;
    }

    public Long getQuestionId () {
        return questionId;
    }

    public Long getAlternativeId () {
        return alternativeId;
    }
}