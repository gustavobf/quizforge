package com.quizforge.domain.model;

import com.quizforge.domain.enumtype.QuestionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Question Record Tests")
class QuestionTest {

    @Nested
    @DisplayName("Builder Tests")
    class BuilderTests {

        @Test
        @DisplayName("should create question with all fields")
        void shouldCreateQuestionWithAllFields() {
            Subject subject = createSubject();
            List<Alternative> alternatives = createAlternatives();

            Question question = Question.builder()
                    .id(10L)
                    .statement("What is the capital of France?")
                    .alternatives(alternatives)
                    .subject(subject)
                    .type(QuestionType.SINGLE_CHOICE)
                    .build();

            assertEquals(10L, question.id());
            assertEquals("What is the capital of France?", question.statement());
            assertSame(alternatives, question.alternatives());
            assertSame(subject, question.subject());
            assertSame(QuestionType.SINGLE_CHOICE, question.type());
        }

        @Test
        @DisplayName("should allow null values in builder")
        void shouldAllowNullValuesInBuilder() {
            Question question = Question.builder().build();

            assertNull(question.id());
            assertNull(question.statement());
            assertNull(question.alternatives());
            assertNull(question.subject());
            assertNull(question.type());
        }
    }

    @Nested
    @DisplayName("Getter Tests")
    class GetterTests {

        @Test
        @DisplayName("should return constructor values from explicit getters")
        void shouldReturnConstructorValuesFromExplicitGetters() {
            Subject subject = createSubject();
            List<Alternative> alternatives = createAlternatives();
            Question question = new Question(5L, "Statement", alternatives, subject, QuestionType.MULTIPLE_CHOICE);

            assertEquals(5L, question.id());
            assertEquals("Statement", question.statement());
            assertSame(alternatives, question.alternatives());
            assertSame(subject, question.subject());
            assertSame(QuestionType.MULTIPLE_CHOICE, question.type());
        }
    }

    @Nested
    @DisplayName("Correct Answer Tests")
    class CorrectAnswerTests {

        @Test
        @DisplayName("should return true for correct single choice answer")
        void shouldReturnTrueForCorrectSingleChoiceAnswer() {
            Question question = new Question(1L, "Question", createAlternatives(), createSubject(), QuestionType.SINGLE_CHOICE);

            boolean correct = question.isCorrectAnswer(List.of(2L));

            assertTrue(correct);
        }

        @Test
        @DisplayName("should return true for correct multiple choice answer regardless of order and duplicates")
        void shouldReturnTrueForCorrectMultipleChoiceAnswerRegardlessOfOrderAndDuplicates() {
            Question question = new Question(
                    2L,
                    "Question",
                    List.of(
                            new Alternative(1L, "A", true),
                            new Alternative(2L, "B", false),
                            new Alternative(3L, "C", true)),
                    createSubject(),
                    QuestionType.MULTIPLE_CHOICE);

            @SuppressWarnings("unchecked")
            List<Long> selectedAlternativeIds = mock(List.class);
            when(selectedAlternativeIds.iterator()).thenReturn(List.of(3L, 1L, 1L).iterator());

            boolean correct = question.isCorrectAnswer(selectedAlternativeIds);

            assertTrue(correct);
        }

        @ParameterizedTest
        @MethodSource("com.quizforge.domain.model.QuestionTest#incorrectSelections")
        @DisplayName("should return false for incorrect selections")
        void shouldReturnFalseForIncorrectSelections(List<Long> selectedAlternativeIds) {
            Question question = new Question(
                    3L,
                    "Question",
                    List.of(
                            new Alternative(1L, "A", true),
                            new Alternative(2L, "B", false),
                            new Alternative(3L, "C", true)),
                    createSubject(),
                    QuestionType.MULTIPLE_CHOICE);

            boolean correct = question.isCorrectAnswer(selectedAlternativeIds);

            assertFalse(correct);
        }

        @Test
        @DisplayName("should return true when no alternative is correct and selection is empty")
        void shouldReturnTrueWhenNoAlternativeIsCorrectAndSelectionIsEmpty() {
            Question question = new Question(
                    4L,
                    "Question",
                    List.of(
                            new Alternative(1L, "A", false),
                            new Alternative(2L, "B", false)),
                    createSubject(),
                    QuestionType.SINGLE_CHOICE);

            boolean correct = question.isCorrectAnswer(List.of());

            assertTrue(correct);
        }

        @Test
        @DisplayName("should throw null pointer exception when selected alternatives are null")
        void shouldThrowNullPointerExceptionWhenSelectedAlternativesAreNull() {
            Question question = new Question(1L, "Question", createAlternatives(), createSubject(), QuestionType.SINGLE_CHOICE);

            assertThrows(NullPointerException.class, () -> question.isCorrectAnswer(null));
        }

        @Test
        @DisplayName("should throw null pointer exception when alternatives are null")
        void shouldThrowNullPointerExceptionWhenAlternativesAreNull() {
            Question question = new Question(1L, "Question", null, createSubject(), QuestionType.SINGLE_CHOICE);

            assertThrows(NullPointerException.class, () -> question.isCorrectAnswer(List.of(1L)));
        }
    }

    static Stream<List<Long>> incorrectSelections() {
        return Stream.of(
                List.of(1L),
                List.of(1L, 2L, 3L),
                List.of(2L, 3L),
                List.of(99L),
                new ArrayList<>());
    }

    private static Subject createSubject() {
        return Subject.builder()
                .id(1L)
                .name("Geography")
                .description("World geography")
                .build();
    }

    private static List<Alternative> createAlternatives() {
        List<Alternative> alternatives = List.of(
                new Alternative(1L, "London", false),
                new Alternative(2L, "Paris", true),
                new Alternative(3L, "Berlin", false));
        assertNotNull(alternatives);
        return alternatives;
    }
}
