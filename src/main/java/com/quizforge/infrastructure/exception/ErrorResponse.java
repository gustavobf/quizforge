package com.quizforge.infrastructure.exception;

import lombok.*;

import java.time.*;
import java.util.*;

@Getter
@Builder
public class ErrorResponse {

    private LocalDateTime timestamp;
    private Integer status;
    private String error;
    private String message;
    private String details;
    private List<String> errors;
    private Map<String, String> validationErrors;
}