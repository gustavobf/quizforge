package com.quizforge.domain.model;

import com.quizforge.domain.exception.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Subject {

    private Long id;
    private String name;
    private String description;

    public void validate () {
        if (name == null || name.isBlank()) {
            throw new BusinessException("Subject name cannot be empty");
        }
        if (name.length() > 100) {
            throw new BusinessException("Subject name cannot exceed 100 characters");
        }
    }
}