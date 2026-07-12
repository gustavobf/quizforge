package com.quizforge.domain.model;

import lombok.*;

import java.util.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Question {

    private Long id;
    private String statement;
    private List<Alternative> alternatives;
    private Subject subject;

    public boolean isMultipleChoice () {
        if (alternatives == null)
            return false;
        return alternatives.stream().filter(Alternative::isCorrect).count() > 1;
    }

}