package com.quizforge.domain.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Alternative {

    private Long id;

    private String description;

    private boolean correct;
}