package com.quizforge.domain.model;

public record Alternative(Long id, String description, boolean correct) {
    
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String description;
        private boolean correct;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder correct(boolean correct) {
            this.correct = correct;
            return this;
        }

        public Alternative build() {
            return new Alternative(id, description, correct);
        }
    }
}