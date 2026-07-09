package com.quizforge.domain.model;

import lombok.*;

import java.time.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAnswer {

    private Long id;
    private Long examId;
    private Long questionId;
    private Long alternativeId;
    private boolean correct;
    private LocalDateTime answeredAt;
}