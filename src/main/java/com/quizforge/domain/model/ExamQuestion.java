package com.quizforge.domain.model;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExamQuestion {

    private Long id;
    private Question question;
    private int orderNumber;
    private boolean answered;
    private Long selectedAlternativeId;
    private boolean correct;

    public void answer (Long alternativeId) {
        this.selectedAlternativeId = alternativeId;
        this.answered = true;

        this.correct = question.getAlternatives().stream().filter(alt -> alt.getId().equals(alternativeId)).findFirst()
                .map(Alternative::isCorrect).orElse(false);
    }

    public void reset () {
        this.answered = false;
        this.selectedAlternativeId = null;
        this.correct = false;
    }
}