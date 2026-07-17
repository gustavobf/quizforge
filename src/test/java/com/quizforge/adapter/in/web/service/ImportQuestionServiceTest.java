package com.quizforge.adapter.in.web.service;

import com.quizforge.adapter.in.web.dto.request.CreateQuestionRequest;
import com.quizforge.adapter.in.web.dto.request.ImportQuestionRow;
import com.quizforge.application.port.in.CreateQuestionUseCase;
import com.quizforge.application.port.out.ExcelParserPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImportQuestionServiceTest {

    @Mock
    private ExcelParserPort excelParserPort;

    @Mock
    private CreateQuestionUseCase createQuestionUseCase;

    @InjectMocks
    private ImportQuestionService importQuestionService;

    private MockMultipartFile validFile;
    private Long subjectId;

    @BeforeEach
    void setUp () {
        subjectId = 1L;
        validFile = new MockMultipartFile("file", "questions.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "conteudo".getBytes());
    }

    @Test
    void shouldImportQuestionsSuccessfully () {
        List<ImportQuestionRow> mockRows = createMockQuestions();
        when(excelParserPort.parse(any(MockMultipartFile.class))).thenReturn(mockRows);

        importQuestionService.execute(validFile, subjectId);

        verify(createQuestionUseCase, times(mockRows.size())).execute(any(CreateQuestionRequest.class));
    }

    @Test
    void shouldConvertRowToCreateQuestionRequestCorrectly () {
        List<ImportQuestionRow> mockRows = createMockQuestions();
        when(excelParserPort.parse(any(MockMultipartFile.class))).thenReturn(mockRows);

        ArgumentCaptor<CreateQuestionRequest> captor = ArgumentCaptor.forClass(CreateQuestionRequest.class);

        importQuestionService.execute(validFile, subjectId);

        verify(createQuestionUseCase, times(mockRows.size())).execute(captor.capture());
        List<CreateQuestionRequest> capturedRequests = captor.getAllValues();

        assertEquals(2, capturedRequests.size());
        assertEquals("Quanto é 2 + 2?", capturedRequests.get(0).getStatement());
        assertEquals(subjectId, capturedRequests.get(0).getSubjectId());
        assertNotNull(capturedRequests.get(0).getAlternatives());
        assertFalse(capturedRequests.get(0).getAlternatives().isEmpty());

        assertEquals("Qual é a capital do Brasil?", capturedRequests.get(1).getStatement());
        assertEquals(subjectId, capturedRequests.get(1).getSubjectId());
        assertNotNull(capturedRequests.get(1).getAlternatives());
        assertFalse(capturedRequests.get(1).getAlternatives().isEmpty());
    }

    @Test
    void shouldHandleEmptyFile () {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "empty.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[0]);
        when(excelParserPort.parse(any(MockMultipartFile.class))).thenReturn(List.of());

        importQuestionService.execute(emptyFile, subjectId);

        verify(createQuestionUseCase, never()).execute(any());
    }

    @Test
    void shouldHandleMultipleAlternatives () {
        ImportQuestionRow row = ImportQuestionRow.builder().statement("Qual é a capital do Brasil?")
                .subjectId(subjectId).alternatives(new HashMap<>() {{
                    put("A", "Rio de Janeiro");
                    put("B", "Brasília");
                    put("C", "São Paulo");
                    put("D", "Salvador");
                }}).correctAnswers(new HashSet<>(Arrays.asList("B"))).build();

        when(excelParserPort.parse(any(MockMultipartFile.class))).thenReturn(Arrays.asList(row));

        importQuestionService.execute(validFile, subjectId);

        verify(createQuestionUseCase, times(1)).execute(any(CreateQuestionRequest.class));
    }

    @Test
    void shouldHandleMultipleCorrectAnswers () {
        ImportQuestionRow row = ImportQuestionRow.builder().statement("Quais são as cores da bandeira do Brasil?")
                .subjectId(subjectId).alternatives(new HashMap<>() {{
                    put("A", "Verde");
                    put("B", "Amarelo");
                    put("C", "Azul");
                    put("D", "Branco");
                }}).correctAnswers(new HashSet<>(Arrays.asList("A", "B", "C", "D"))).build();

        when(excelParserPort.parse(any(MockMultipartFile.class))).thenReturn(Arrays.asList(row));

        importQuestionService.execute(validFile, subjectId);

        verify(createQuestionUseCase, times(1)).execute(any(CreateQuestionRequest.class));
    }

    @Test
    void shouldHandleParserException () {
        when(excelParserPort.parse(any(MockMultipartFile.class))).thenThrow(
                new RuntimeException("Erro ao ler arquivo Excel"));

        assertThrows(RuntimeException.class, () -> importQuestionService.execute(validFile, subjectId));

        verify(createQuestionUseCase, never()).execute(any());
    }

    @Test
    void shouldVerifyCorrectAlternativeMapping () {
        ImportQuestionRow row = ImportQuestionRow.builder().statement("Quanto é 2 + 2?").subjectId(subjectId)
                .alternatives(new HashMap<>() {{
                    put("A", "3");
                    put("B", "4");
                    put("C", "5");
                }}).correctAnswers(new HashSet<>(Collections.singletonList("B"))).build();

        when(excelParserPort.parse(any(MockMultipartFile.class))).thenReturn(Arrays.asList(row));

        ArgumentCaptor<CreateQuestionRequest> captor = ArgumentCaptor.forClass(CreateQuestionRequest.class);

        importQuestionService.execute(validFile, subjectId);

        verify(createQuestionUseCase).execute(captor.capture());
        CreateQuestionRequest request = captor.getValue();

        assertNotNull(request.getAlternatives());
        assertEquals(3, request.getAlternatives().size());

        request.getAlternatives().forEach(alt -> {
            if (alt.getDescription().equals("4")) {
                assertTrue(alt.getCorrect());
            } else {
                assertFalse(alt.getCorrect());
            }
        });
    }

    private List<ImportQuestionRow> createMockQuestions () {
        ImportQuestionRow row1 = ImportQuestionRow.builder().statement("Quanto é 2 + 2?").subjectId(subjectId)
                .alternatives(new HashMap<>() {{
                    put("A", "3");
                    put("B", "4");
                    put("C", "5");
                }}).correctAnswers(new HashSet<>(Collections.singletonList("B"))).build();

        ImportQuestionRow row2 = ImportQuestionRow.builder().statement("Qual é a capital do Brasil?")
                .subjectId(subjectId).alternatives(new HashMap<>() {{
                    put("A", "Rio de Janeiro");
                    put("B", "Brasília");
                    put("C", "São Paulo");
                }}).correctAnswers(new HashSet<>(Collections.singletonList("B"))).build();

        return Arrays.asList(row1, row2);
    }

    @Test
    void shouldProcessAllQuestionsEvenIfOneFails () {
        List<ImportQuestionRow> mockRows = createMockQuestions();
        when(excelParserPort.parse(any(MockMultipartFile.class))).thenReturn(mockRows);

        doThrow(new RuntimeException("Erro ao salvar questão")).when(createQuestionUseCase)
                .execute(any(CreateQuestionRequest.class));

        assertThrows(RuntimeException.class, () -> importQuestionService.execute(validFile, subjectId));

        verify(createQuestionUseCase, atLeastOnce()).execute(any());
    }
}