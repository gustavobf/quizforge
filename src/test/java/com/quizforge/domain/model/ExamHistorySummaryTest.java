package com.quizforge.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ExamHistorySummary Record Tests")
class ExamHistorySummaryTest {

    @Nested
    @DisplayName("Builder Tests")
    class BuilderTests {

        @Test
        @DisplayName("should create summary with all fields")
        void shouldCreateSummaryWithAllFields() {
            ExamHistory recentExam = createExamHistory();
            List<ExamHistory> recentExams = new ArrayList<>(List.of(recentExam));

            ExamHistorySummary summary = ExamHistorySummary.builder()
                    .totalExams(5)
                    .totalQuestionsAnswered(50)
                    .totalCorrectAnswers(40)
                    .averageScore(80.0)
                    .bestScore(95)
                    .worstScore(65)
                    .recentExams(recentExams)
                    .build();

            assertEquals(5, summary.totalExams());
            assertEquals(50, summary.totalQuestionsAnswered());
            assertEquals(40, summary.totalCorrectAnswers());
            assertEquals(80.0, summary.averageScore());
            assertEquals(95, summary.bestScore());
            assertEquals(65, summary.worstScore());
            assertEquals(List.of(recentExam), summary.recentExams());
        }

        @Test
        @DisplayName("should create summary with empty recent exams when builder receives null")
        void shouldCreateSummaryWithEmptyRecentExamsWhenBuilderReceivesNull() {
            ExamHistorySummary summary = ExamHistorySummary.builder().recentExams(null).build();

            assertEquals(0, summary.totalExams());
            assertEquals(0, summary.totalQuestionsAnswered());
            assertEquals(0, summary.totalCorrectAnswers());
            assertEquals(0.0, summary.averageScore());
            assertEquals(0, summary.bestScore());
            assertEquals(0, summary.worstScore());
            assertTrue(summary.recentExams().isEmpty());
        }
    }

    @Nested
    @DisplayName("Compact Constructor Tests")
    class CompactConstructorTests {

        @Test
        @DisplayName("should return unmodifiable recent exams list")
        void shouldReturnUnmodifiableRecentExamsList() {
            ExamHistorySummary summary = new ExamHistorySummary(1, 10, 7, 70.0, 90, 50, new ArrayList<>(List.of(createExamHistory())));

            assertThrows(UnsupportedOperationException.class, () -> summary.recentExams().add(createExamHistory()));
        }

        @Test
        @DisplayName("should replace null recent exams with empty list")
        void shouldReplaceNullRecentExamsWithEmptyList() {
            ExamHistorySummary summary = new ExamHistorySummary(1, 2, 3, 4.0, 5, 6, null);

            assertTrue(summary.recentExams().isEmpty());
        }
    }

    @Nested
    @DisplayName("Accessor Tests")
    class AccessorTests {

        @Test
        @DisplayName("should return constructor values from record accessors")
        void shouldReturnConstructorValuesFromRecordAccessors() {
            ExamHistory recentExam = createExamHistory();
            ExamHistorySummary summary = new ExamHistorySummary(3, 30, 21, 70.0, 90, 60, List.of(recentExam));

            assertEquals(3, summary.totalExams());
            assertEquals(30, summary.totalQuestionsAnswered());
            assertEquals(21, summary.totalCorrectAnswers());
            assertEquals(70.0, summary.averageScore());
            assertEquals(90, summary.bestScore());
            assertEquals(60, summary.worstScore());
            assertEquals(List.of(recentExam), summary.recentExams());
        }
    }

    private static ExamHistory createExamHistory() {
        return new ExamHistory(
                1L,
                "Exam",
                "History",
                10,
                8,
                2,
                80.0,
                LocalDateTime.of(2026, 5, 1, 9, 0),
                LocalDateTime.of(2026, 5, 1, 9, 20),
                20);
    }
}
