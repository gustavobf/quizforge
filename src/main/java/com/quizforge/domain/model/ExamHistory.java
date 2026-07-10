package com.quizforge.domain.model;

import com.quizforge.domain.enumtype.*;
import lombok.*;

import java.time.*;

@Getter
@Builder
public class ExamHistory {
    private Long id;
    private String title;
    private String subjectName;
    private int totalQuestions;
    private int correctAnswers;
    private int wrongAnswers;
    private double score;
    private ExamStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private int timeSpentInMinutes;
}