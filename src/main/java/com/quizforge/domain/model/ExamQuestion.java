package com.quizforge.domain.model;

import lombok.*;

import java.util.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExamQuestion {

    private Long id;
    private Question question;
    private Integer orderNumber;
    private boolean answered;
    private boolean correct;
    private List<Long> selectedAlternativeIds;

    public boolean isAnswered () {
        return answered;
    }

    public boolean isCorrect () {
        return correct;
    }

    public List<Long> getSelectedAlternativeIds () {
        return selectedAlternativeIds != null ? selectedAlternativeIds : new ArrayList<>();
    }

    public void setAnswered (boolean answered) {
        this.answered = answered;
    }

    public void setCorrect (boolean correct) {
        this.correct = correct;
    }

    public void setSelectedAlternativeIds (List<Long> selectedAlternativeIds) {
        this.selectedAlternativeIds = selectedAlternativeIds != null ? selectedAlternativeIds : new ArrayList<>();
    }

    public Question getQuestion () {
        return question;
    }

    public Integer getOrderNumber () {
        return orderNumber;
    }
}