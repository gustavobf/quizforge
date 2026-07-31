package com.quizforge.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("UserAnswer Record Tests")
class UserAnswerTest {

    @Nested
    @DisplayName("Builder Tests")
    class BuilderTests {

        @Test
        @DisplayName("should create user answer with all fields")
        void shouldCreateUserAnswerWithAllFields() {
            LocalDateTime answeredAt = LocalDateTime.of(2026, 1, 2, 10, 30);
            List<Long> alternativeIds = List.of(11L, 12L);

            UserAnswer userAnswer = UserAnswer.builder()
                    .id(1L)
                    .examId(2L)
                    .questionId(3L)
                    .alternativeIds(alternativeIds)
                    .correct(true)
                    .answeredAt(answeredAt)
                    .build();

            assertEquals(1L, userAnswer.id());
            assertEquals(2L, userAnswer.examId());
            assertEquals(3L, userAnswer.questionId());
            assertEquals(List.of(11L, 12L), userAnswer.alternativeIds());
            assertTrue(userAnswer.correct());
            assertEquals(answeredAt, userAnswer.answeredAt());
        }

        @Test
        @DisplayName("should create user answer with empty alternatives when builder receives null")
        void shouldCreateUserAnswerWithEmptyAlternativesWhenBuilderReceivesNull() {
            UserAnswer userAnswer = UserAnswer.builder().alternativeIds(null).build();

            assertTrue(userAnswer.alternativeIds().isEmpty());
        }
    }

    @Nested
    @DisplayName("Compact Constructor Tests")
    class CompactConstructorTests {

        @Test
        @DisplayName("should create defensive copy for alternative ids")
        void shouldCreateDefensiveCopyForAlternativeIds() {
            List<Long> sourceAlternativeIds = Mockito.spy(new ArrayList<>(List.of(1L, 2L)));

            UserAnswer userAnswer = new UserAnswer(1L, 2L, 3L, sourceAlternativeIds, false, null);
            sourceAlternativeIds.add(99L);

            assertEquals(List.of(1L, 2L), userAnswer.alternativeIds());
            assertNotSame(sourceAlternativeIds, userAnswer.alternativeIds());
        }

        @Test
        @DisplayName("should return unmodifiable alternative ids")
        void shouldReturnUnmodifiableAlternativeIds() {
            UserAnswer userAnswer = new UserAnswer(1L, 2L, 3L, List.of(4L, 5L), false, null);

            assertThrows(UnsupportedOperationException.class, () -> userAnswer.alternativeIds().add(6L));
        }
    }

    @Nested
    @DisplayName("Getter Tests")
    class GetterTests {

        @Test
        @DisplayName("should return constructor values from explicit getters")
        void shouldReturnConstructorValuesFromExplicitGetters() {
            LocalDateTime answeredAt = LocalDateTime.of(2025, 12, 31, 23, 59);
            UserAnswer userAnswer = new UserAnswer(9L, 8L, 7L, List.of(), false, answeredAt);

            assertEquals(9L, userAnswer.id());
            assertEquals(8L, userAnswer.examId());
            assertEquals(7L, userAnswer.questionId());
            assertTrue(userAnswer.alternativeIds().isEmpty());
            assertFalse(userAnswer.correct());
            assertEquals(answeredAt, userAnswer.answeredAt());
        }
    }
}
