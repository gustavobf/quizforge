package com.quizforge.domain.model;

import com.quizforge.domain.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Subject Class Tests")
class SubjectTest {

    @Nested
    @DisplayName("Builder And Getter Tests")
    class BuilderAndGetterTests {

        @Test
        @DisplayName("should create subject with all fields")
        void shouldCreateSubjectWithAllFields() {
            Subject subject = Subject.builder()
                    .id(1L)
                    .name("Mathematics")
                    .description("Numbers and equations")
                    .build();

            assertEquals(1L, subject.getId());
            assertEquals("Mathematics", subject.getName());
            assertEquals("Numbers and equations", subject.getDescription());
        }

        @Test
        @DisplayName("should allow null description")
        void shouldAllowNullDescription() {
            Subject subject = Subject.builder()
                    .id(2L)
                    .name("History")
                    .description(null)
                    .build();

            assertEquals(2L, subject.getId());
            assertEquals("History", subject.getName());
            assertNull(subject.getDescription());
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   ", "\t"})
        @DisplayName("should throw business exception when name is blank")
        void shouldThrowBusinessExceptionWhenNameIsBlank(String name) {
            // Arrange & Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> Subject.builder().name(name).description("Description").build());
            assertEquals("Subject name cannot be empty", exception.getMessage());
        }

        @Test
        @DisplayName("should throw business exception when name exceeds one hundred characters")
        void shouldThrowBusinessExceptionWhenNameExceedsOneHundredCharacters() {
            String name = "a".repeat(101);

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> Subject.builder().name(name).description("Description").build());
            assertEquals("Subject name cannot exceed 100 characters", exception.getMessage());
        }

        @Test
        @DisplayName("should accept name with exactly one hundred characters")
        void shouldAcceptNameWithExactlyOneHundredCharacters() {
            String name = "a".repeat(100);

            Subject subject = Subject.builder().id(1L).name(name).description("Description").build();

            assertEquals(name, subject.getName());
        }
    }

    @Nested
    @DisplayName("Object Contract Tests")
    class ObjectContractTests {

        @Test
        @DisplayName("should consider subjects with same non null id equal")
        void shouldConsiderSubjectsWithSameNonNullIdEqual() {
            Subject first = Subject.builder().id(1L).name("Math").description("A").build();
            Subject second = Subject.builder().id(1L).name("Science").description("B").build();

            assertEquals(first, second);
            assertEquals(first.hashCode(), second.hashCode());
        }

        @Test
        @DisplayName("should consider subjects with different ids not equal")
        void shouldConsiderSubjectsWithDifferentIdsNotEqual() {
            Subject first = Subject.builder().id(1L).name("Math").description("A").build();
            Subject second = Subject.builder().id(2L).name("Math").description("A").build();

            assertNotEquals(first, second);
        }

        @Test
        @DisplayName("should consider subjects with null ids not equal")
        void shouldConsiderSubjectsWithNullIdsNotEqual() {
            Subject first = Subject.builder().name("Math").description("A").build();
            Subject second = Subject.builder().name("Math").description("A").build();

            assertNotEquals(first, second);
            assertEquals(0, first.hashCode());
        }

        @Test
        @DisplayName("should render readable to string")
        void shouldRenderReadableToString() {
            Subject subject = Subject.builder().id(3L).name("Physics").description("Study of matter").build();

            String value = subject.toString();

            assertTrue(value.contains("id=3"));
            assertTrue(value.contains("name='Physics'"));
            assertTrue(value.contains("description='Study of matter'"));
        }
    }
}
