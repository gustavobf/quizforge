package com.quizforge.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record UserAnswer(
        Long id,
        Long examId,
        Long questionId,
        List<Long> alternativeIds,
        boolean correct,
        LocalDateTime answeredAt
) {

    public UserAnswer {
        alternativeIds = alternativeIds == null ? new ArrayList<>() : Collections.unmodifiableList(new ArrayList<>(alternativeIds));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long examId;
        private Long questionId;
        private List<Long> alternativeIds;
        private boolean correct;
        private LocalDateTime answeredAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder examId(Long examId) {
            this.examId = examId;
            return this;
        }

        public Builder questionId(Long questionId) {
            this.questionId = questionId;
            return this;
        }

        public Builder alternativeIds(List<Long> alternativeIds) {
            this.alternativeIds = alternativeIds;
            return this;
        }

        public Builder correct(boolean correct) {
            this.correct = correct;
            return this;
        }

        public Builder answeredAt(LocalDateTime answeredAt) {
            this.answeredAt = answeredAt;
            return this;
        }

        public UserAnswer build() {
            return new UserAnswer(id, examId, questionId, alternativeIds, correct, answeredAt);
        }
    }
}