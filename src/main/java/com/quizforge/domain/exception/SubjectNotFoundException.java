package com.quizforge.domain.exception;

import lombok.*;

@Getter
public class SubjectNotFoundException extends RuntimeException {

    private final Long subjectId;

    public SubjectNotFoundException (Long subjectId) {
        super("Subject not found with id: " + subjectId);
        this.subjectId = subjectId;
    }

    public SubjectNotFoundException (String message, Long subjectId) {
        super(message);
        this.subjectId = subjectId;
    }
}