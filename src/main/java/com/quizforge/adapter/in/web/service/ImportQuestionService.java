package com.quizforge.adapter.in.web.service;

import com.quizforge.adapter.in.web.dto.request.*;
import com.quizforge.adapter.in.web.dto.response.*;
import com.quizforge.application.port.in.*;
import com.quizforge.application.port.out.*;
import lombok.*;
import org.springframework.stereotype.*;
import org.springframework.web.multipart.*;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ImportQuestionService implements ImportQuestionUseCase {

    private final CreateQuestionUseCase createQuestionUseCase;
    private final ExcelParserPort excelParserPort;

    @Override
    public ImportQuestionsResponse execute (MultipartFile file, Long subjectId) {
        List<ImportQuestionRow> rows = excelParserPort.parse(file);

        rows.stream().map(row -> toCreateQuestionRequest(row, subjectId)).forEach(createQuestionUseCase::execute);

        return new ImportQuestionsResponse(rows.size());
    }

    private CreateQuestionRequest toCreateQuestionRequest (ImportQuestionRow row, Long subjectId) {
        List<CreateAlternativeRequest> alternatives = row.getAlternatives().entrySet().stream()
                .map(entry -> CreateAlternativeRequest.builder().description(entry.getValue())
                        .correct(row.getCorrectAnswers().contains(entry.getKey())).build()).toList();

        return CreateQuestionRequest.builder().statement(row.getStatement()).subjectId(subjectId)
                .alternatives(alternatives).build();
    }
}