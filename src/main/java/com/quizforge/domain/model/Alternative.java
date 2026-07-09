package com.quizforge.domain.model;

import lombok.*;

@Getter
@Builder
public class Alternative {

    private Long id;

    private String description;

    private boolean correct;
}