package com.quizforge.domain.model;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExamQuestion {

    private Long id;
    private Question question;
    private Integer orderNumber;

}