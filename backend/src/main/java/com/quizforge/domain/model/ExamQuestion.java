package com.quizforge.domain.model;

public record ExamQuestion(Long id, Question question, Integer orderNumber) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Question question;
        private Integer orderNumber;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder question(Question question) {
            this.question = question;
            return this;
        }

        public Builder orderNumber(Integer orderNumber) {
            this.orderNumber = orderNumber;
            return this;
        }

        public ExamQuestion build() {
            return new ExamQuestion(id, question, orderNumber);
        }
    }
}