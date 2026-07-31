package com.quizforge.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Alternative Record Tests")
class AlternativeTest {

    @Nested
    @DisplayName("Builder Tests")
    class BuilderTests {

        @Test
        @DisplayName("should create alternative with all fields")
        void shouldCreateAlternativeWithAllFields() {
            Long id = 1L;
            String description = "Paris";

            Alternative alternative = Alternative.builder()
                    .id(id)
                    .description(description)
                    .correct(true)
                    .build();

            assertEquals(id, alternative.id());
            assertEquals(description, alternative.description());
            assertTrue(alternative.correct());
        }

        @Test
        @DisplayName("should create alternative with null values and default correct flag")
        void shouldCreateAlternativeWithNullValuesAndDefaultCorrectFlag() {
            Alternative alternative = Alternative.builder().build();

            assertNull(alternative.id());
            assertNull(alternative.description());
            assertFalse(alternative.correct());
        }
    }

    @Nested
    @DisplayName("Getter Tests")
    class GetterTests {

        @Test
        @DisplayName("should return constructor values from explicit getters")
        void shouldReturnConstructorValuesFromExplicitGetters() {
            Alternative alternative = new Alternative(7L, "Option A", false);

            assertEquals(7L, alternative.id());
            assertEquals("Option A", alternative.description());
            assertFalse(alternative.correct());
        }
    }
}
