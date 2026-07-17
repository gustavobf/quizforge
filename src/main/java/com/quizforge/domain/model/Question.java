package com.quizforge.domain.model;

import com.quizforge.domain.enumtype.*;
import lombok.*;

import java.util.*;
import java.util.stream.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Question {

    private Long id;
    private String statement;
    private List<Alternative> alternatives;
    private Subject subject;
    private QuestionType type;

    public boolean isCorrectAnswer(
            List<Long> selectedAlternativeIds) {

        Set<Long> correctIds =
                alternatives.stream()
                        .filter(Alternative::isCorrect)
                        .map(Alternative::getId)
                        .collect(Collectors.toSet());

        return correctIds.equals(
                new HashSet<>(selectedAlternativeIds)
        );
    }

}