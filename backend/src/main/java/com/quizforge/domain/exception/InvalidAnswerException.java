package com.quizforge.domain.exception;

import java.util.*;

public class InvalidAnswerException extends QuestionException {

    private final Long questionId;
    private final List<Long> alternativeIds;

    public InvalidAnswerException (Long questionId, List<Long> alternativeIds) {
        super("Invalid answer for question " + questionId + ": alternatives " + alternativeIds, "INVALID_ANSWER");
        this.questionId = questionId;
        this.alternativeIds = alternativeIds != null ? new ArrayList<>(alternativeIds) : new ArrayList<>();
    }

    public Long getQuestionId () {
        return questionId;
    }

    public List<Long> getAlternativeIds () {
        return alternativeIds != null ? new ArrayList<>(alternativeIds) : new ArrayList<>();
    }

}