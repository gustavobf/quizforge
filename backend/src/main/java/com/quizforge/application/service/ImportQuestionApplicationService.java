package com.quizforge.application.service;

import com.quizforge.adapter.in.web.dto.request.CreateAlternativeRequest;
import com.quizforge.adapter.in.web.dto.request.CreateQuestionRequest;
import com.quizforge.adapter.in.web.dto.request.ImportQuestionRow;
import com.quizforge.adapter.in.web.dto.response.ImportQuestionsResponse;
import com.quizforge.application.port.in.CreateQuestionUseCase;
import com.quizforge.application.port.in.ImportQuestionUseCase;
import com.quizforge.application.port.out.ExcelParserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImportQuestionApplicationService implements ImportQuestionUseCase {

    private final CreateQuestionUseCase createQuestionUseCase;
    private final ExcelParserPort excelParserPort;

    @Override
    public ImportQuestionsResponse execute(MultipartFile file, Long subjectId) {
        List<ImportQuestionRow> rows = excelParserPort.parse(file);
        rows.stream()
                .map(row -> toCreateQuestionRequest(row, subjectId))
                .forEach(createQuestionUseCase::execute);
        return new ImportQuestionsResponse(rows.size());
    }

    private CreateQuestionRequest toCreateQuestionRequest(ImportQuestionRow row, Long subjectId) {
        List<CreateAlternativeRequest> alternatives = row.getAlternatives()
                .entrySet()
                .stream()
                .map(entry -> CreateAlternativeRequest.builder()
                        .description(entry.getValue())
                        .correct(row.getCorrectAnswers().contains(entry.getKey()))
                        .build())
                .toList();

        return CreateQuestionRequest.builder()
                .statement(row.getStatement())
                .subjectId(subjectId)
                .alternatives(alternatives)
                .build();
    }
}
