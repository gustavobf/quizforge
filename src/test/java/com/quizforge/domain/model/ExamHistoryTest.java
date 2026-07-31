package com.quizforge.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("ExamHistory Record Tests")
class ExamHistoryTest {

    @Nested
    @DisplayName("Builder Tests")
    class BuilderTests {

        @Test
        @DisplayName("should create exam history with all fields")
        void shouldCreateExamHistoryWithAllFields() {
            LocalDateTime startedAt = LocalDateTime.of(2026, 2, 1, 8, 0);
            LocalDateTime finishedAt = LocalDateTime.of(2026, 2, 1, 8, 45);

            ExamHistory examHistory = ExamHistory.builder()
                    .id(1L)
                    .title("Midterm")
                    .subjectName("Math")
                    .totalQuestions(20)
                    .correctAnswers(16)
                    .wrongAnswers(4)
                    .score(80.0)
                    .startedAt(startedAt)
                    .finishedAt(finishedAt)
                    .timeSpentInMinutes(45)
                    .build();

            assertEquals(1L, examHistory.id());
            assertEquals("Midterm", examHistory.title());
            assertEquals("Math", examHistory.subjectName());
            assertEquals(20, examHistory.totalQuestions());
            assertEquals(16, examHistory.correctAnswers());
            assertEquals(4, examHistory.wrongAnswers());
            assertEquals(80.0, examHistory.score());
            assertEquals(startedAt, examHistory.startedAt());
            assertEquals(finishedAt, examHistory.finishedAt());
            assertEquals(45, examHistory.timeSpentInMinutes());
        }

        @Test
        @DisplayName("should create exam history with default primitive values and null references")
        void shouldCreateExamHistoryWithDefaultPrimitiveValuesAndNullReferences() {
            ExamHistory examHistory = ExamHistory.builder().build();

            assertNull(examHistory.id());
            assertNull(examHistory.title());
            assertNull(examHistory.subjectName());
            assertEquals(0, examHistory.totalQuestions());
            assertEquals(0, examHistory.correctAnswers());
            assertEquals(0, examHistory.wrongAnswers());
            assertEquals(0.0, examHistory.score());
            assertNull(examHistory.startedAt());
            assertNull(examHistory.finishedAt());
            assertEquals(0, examHistory.timeSpentInMinutes());
        }
    }

    @Nested
    @DisplayName("Accessor Tests")
    class AccessorTests {

        @Test
        @DisplayName("should return constructor values from record accessors")
        void shouldReturnConstructorValuesFromRecordAccessors() {
            LocalDateTime startedAt = LocalDateTime.of(2026, 3, 10, 14, 0);
            LocalDateTime finishedAt = LocalDateTime.of(2026, 3, 10, 14, 30);
            ExamHistory examHistory = new ExamHistory(2L, "Quiz", "Science", 10, 7, 3, 70.5, startedAt, finishedAt, 30);

            assertEquals(2L, examHistory.id());
            assertEquals("Quiz", examHistory.title());
            assertEquals("Science", examHistory.subjectName());
            assertEquals(10, examHistory.totalQuestions());
            assertEquals(7, examHistory.correctAnswers());
            assertEquals(3, examHistory.wrongAnswers());
            assertEquals(70.5, examHistory.score());
            assertEquals(startedAt, examHistory.startedAt());
            assertEquals(finishedAt, examHistory.finishedAt());
            assertEquals(30, examHistory.timeSpentInMinutes());
        }
    }
}
