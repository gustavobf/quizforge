package com.quizforge.domain.model;

import com.quizforge.domain.enumtype.*;
import com.quizforge.domain.exception.*;
import lombok.*;

import java.util.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Question {

    private Long id;
    private String statement;
    private Subject subject;
    private List<Alternative> alternatives;

    public List<Alternative> getAlternativesWithoutCorrectAnswer () {
        if (alternatives == null) {
            return List.of();
        }

        return alternatives.stream()
                .map(alt -> Alternative.builder().id(alt.getId()).description(alt.getDescription()).correct(false).build())
                .toList();
    }
}