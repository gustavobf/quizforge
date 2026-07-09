package com.quizforge.adapter.in.web.service;

import com.quizforge.adapter.in.web.dto.request.*;
import com.quizforge.application.port.in.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;
import org.springframework.mock.web.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImportQuestionServiceTest {

    @Mock
    private CreateQuestionUseCase createQuestionUseCase;

    @InjectMocks
    private ImportQuestionService importQuestionService;

    private MockMultipartFile validFile;

    @BeforeEach
    void setUp () {
        validFile = new MockMultipartFile("file", "questions.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[0]);
    }

    @Test
    void shouldImportQuestionsSuccessfully () {
        Long subjectId = 1L;

        importQuestionService.execute(validFile, subjectId);

        verify(createQuestionUseCase, times(0)).execute(any(CreateQuestionRequest.class));
    }
}