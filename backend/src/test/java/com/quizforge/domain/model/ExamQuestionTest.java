package com.quizforge.domain.model;

import com.quizforge.domain.enumtype.QuestionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

@DisplayName("ExamQuestion Record Tests")
class ExamQuestionTest {

    @Nested
    @DisplayName("Builder Tests")
    class BuilderTests {

        @Test
        @DisplayName("should create exam question with all fields")
        void shouldCreateExamQuestionWithAllFields() {
            Question question = createQuestion();

            ExamQuestion examQuestion = ExamQuestion.builder()
                    .id(100L)
                    .question(question)
                    .orderNumber(3)
                    .build();

            assertEquals(100L, examQuestion.id());
            assertSame(question, examQuestion.question());
            assertEquals(3, examQuestion.orderNumber());
        }

        @Test
        @DisplayName("should allow null values in builder")
        void shouldAllowNullValuesInBuilder() {
            ExamQuestion examQuestion = ExamQuestion.builder().build();

            assertNull(examQuestion.id());
            assertNull(examQuestion.question());
            assertNull(examQuestion.orderNumber());
        }
    }

    @Nested
    @DisplayName("Getter Tests")
    class GetterTests {

        @Test
        @DisplayName("should return constructor values from explicit getters")
        void shouldReturnConstructorValuesFromExplicitGetters() {
            Question question = createQuestion();
            ExamQuestion examQuestion = new ExamQuestion(9L, question, 1);

            assertEquals(9L, examQuestion.id());
            assertSame(question, examQuestion.question());
            assertEquals(1, examQuestion.orderNumber());
        }
    }

    private static Question createQuestion() {
        return new Question(
                1L,
                "Question",
                List.of(new Alternative(1L, "A", true)),
                Subject.builder().id(1L).name("Math").description("Numbers").build(),
                QuestionType.SINGLE_CHOICE);
    }
}
