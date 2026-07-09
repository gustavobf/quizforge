package com.quizforge.domain.enumtype;

import lombok.*;

@Getter
public enum ExamStatus {
    NOT_STARTED("Not Started"), IN_PROGRESS("In Progress"), COMPLETED("Completed");

    private final String description;

    ExamStatus (String description) {
        this.description = description;
    }
}