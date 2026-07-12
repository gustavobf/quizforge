package com.quizforge.domain.model;

import lombok.*;

import java.time.*;
import java.util.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAnswer {

    private Long id;
    private Long examId;
    private Long questionId;
    private List<Long> alternativeIds;
    private boolean correct;
    private LocalDateTime answeredAt;

    public List<Long> getAlternativeIds () {
        return alternativeIds != null ? alternativeIds : new ArrayList<>();
    }

    public void setAlternativeIds (List<Long> alternativeIds) {
        this.alternativeIds = alternativeIds;
    }
}