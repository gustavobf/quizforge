package com.quizforge.domain.enumtype;

public enum QuestionType {
    SINGLE_CHOICE("Single Choice"), MULTIPLE_CHOICE("Multiple Choice");

    private final String description;

    QuestionType (String description) {
        this.description = description;
    }

    public String getDescription () {
        return description;
    }
}