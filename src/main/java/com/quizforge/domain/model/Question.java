package com.quizforge.domain.model;

import com.quizforge.domain.enumtype.QuestionType;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record Question(
        Long id,
        String statement,
        List<Alternative> alternatives,
        Subject subject,
        QuestionType type
) {

    public static Builder builder() {
        return new Builder();
    }

    public boolean isCorrectAnswer(List<Long> selectedAlternativeIds) {
        Set<Long> correctIds = alternatives.stream()
                .filter(Alternative::correct)
                .map(Alternative::id)
                .collect(Collectors.toSet());

        return correctIds.equals(new HashSet<>(selectedAlternativeIds));
    }

    public static class Builder {
        private Long id;
        private String statement;
        private List<Alternative> alternatives;
        private Subject subject;
        private QuestionType type;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder statement(String statement) {
            this.statement = statement;
            return this;
        }

        public Builder alternatives(List<Alternative> alternatives) {
            this.alternatives = alternatives;
            return this;
        }

        public Builder subject(Subject subject) {
            this.subject = subject;
            return this;
        }

        public Builder type(QuestionType type) {
            this.type = type;
            return this;
        }

        public Question build() {
            return new Question(id, statement, alternatives, subject, type);
        }
    }
}