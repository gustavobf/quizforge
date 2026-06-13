package com.quizforge.domain.entity;

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
    private List<Alternative> alternatives;

    public void validate() {
        long correctAnswers =
                alternatives.stream()
                        .filter(Alternative::isCorrect)
                        .count();

        if(correctAnswers != 1) {
            throw new BusinessException(
                    "Question must have exactly one correct alternative");
        }
    }
}