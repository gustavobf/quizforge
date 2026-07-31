package com.quizforge.domain.model;

import com.quizforge.domain.enumtype.QuestionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Exam Class Tests")
class ExamTest {

    @Nested
    @DisplayName("Builder And Getter Tests")
    class BuilderAndGetterTests {

        @Test
        @DisplayName("should create exam with all fields")
        void shouldCreateExamWithAllFields() {
            Subject subject = createSubject();
            List<ExamQuestion> questions = new ArrayList<>(List.of(createExamQuestion()));
            LocalDateTime startedAt = LocalDateTime.of(2026, 6, 1, 10, 0);
            LocalDateTime finishedAt = LocalDateTime.of(2026, 6, 1, 10, 30);

            Exam exam = Exam.builder()
                    .id(1L)
                    .title("Sample Exam")
                    .subject(subject)
                    .questions(questions)
                    .totalQuestions(1)
                    .startedAt(startedAt)
                    .finishedAt(finishedAt)
                    .score(90.0)
                    .build();

            assertEquals(1L, exam.getId());
            assertEquals("Sample Exam", exam.getTitle());
            assertEquals(subject, exam.getSubject());
            assertEquals(questions, exam.getQuestions());
            assertEquals(1, exam.getTotalQuestions());
            assertEquals(startedAt, exam.getStartedAt());
            assertEquals(finishedAt, exam.getFinishedAt());
            assertEquals(90.0, exam.getScore());
            assertTrue(exam.isStarted());
            assertTrue(exam.isFinished());
        }

        @Test
        @DisplayName("should create empty immutable questions list when builder receives null")
        void shouldCreateEmptyImmutableQuestionsListWhenBuilderReceivesNull() {
            Exam exam = Exam.builder().questions(null).build();

            assertTrue(exam.getQuestions().isEmpty());
            assertThrows(UnsupportedOperationException.class, () -> exam.getQuestions().add(createExamQuestion()));
        }

        @Test
        @DisplayName("should expose immutable questions list")
        void shouldExposeImmutableQuestionsList() {
            Exam exam = Exam.builder().questions(new ArrayList<>(List.of(createExamQuestion()))).build();

            assertThrows(UnsupportedOperationException.class, () -> exam.getQuestions().clear());
        }
    }

    @Nested
    @DisplayName("Start Tests")
    class StartTests {

        @Test
        @DisplayName("should start exam and set started state")
        void shouldStartExamAndSetStartedState() {
            Exam exam = Exam.builder().build();

            exam.start();

            assertTrue(exam.isStarted());
            assertNotNull(exam.getStartedAt());
            assertNull(exam.getFinishedAt());
            assertNull(exam.getScore());
        }

        @Test
        @DisplayName("should throw when starting an already started exam")
        void shouldThrowWhenStartingAnAlreadyStartedExam() {
            Exam exam = Exam.builder().startedAt(LocalDateTime.of(2026, 6, 1, 10, 0)).build();

            IllegalStateException exception = assertThrows(IllegalStateException.class, exam::start);
            assertEquals("Exam already started", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Finish Tests")
    class FinishTests {

        @Test
        @DisplayName("should finish started exam and set score")
        void shouldFinishStartedExamAndSetScore() {
            Exam exam = Exam.builder().build();
            exam.start();

            exam.finish(87.5);

            assertTrue(exam.isFinished());
            assertNotNull(exam.getFinishedAt());
            assertEquals(87.5, exam.getScore());
        }

        @Test
        @DisplayName("should throw when finishing exam before start")
        void shouldThrowWhenFinishingExamBeforeStart() {
            Exam exam = Exam.builder().build();

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> exam.finish(50.0));
            assertEquals("Exam not started", exception.getMessage());
            assertNull(exam.getFinishedAt());
            assertNull(exam.getScore());
        }

        @Test
        @DisplayName("should throw when finishing an already finished exam")
        void shouldThrowWhenFinishingAnAlreadyFinishedExam() {
            Exam exam = Exam.builder()
                    .startedAt(LocalDateTime.of(2026, 6, 1, 10, 0))
                    .finishedAt(LocalDateTime.of(2026, 6, 1, 10, 30))
                    .score(60.0)
                    .build();

            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> exam.finish(70.0));
            assertEquals("Exam already finished", exception.getMessage());
            assertEquals(60.0, exam.getScore());
        }
    }

    private static Subject createSubject() {
        return Subject.builder()
                .id(1L)
                .name("Science")
                .description("General science")
                .build();
    }

    private static ExamQuestion createExamQuestion() {
        Question question = new Question(
                1L,
                "Question",
                List.of(new Alternative(1L, "Correct", true)),
                createSubject(),
                QuestionType.SINGLE_CHOICE);
        return new ExamQuestion(10L, question, 1);
    }
}
