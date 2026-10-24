package com.quizforge.adapter.in.web.service;

import com.quizforge.application.port.in.*;
import com.quizforge.application.port.out.*;
import lombok.*;
import org.springframework.stereotype.*;

@Service
@RequiredArgsConstructor
public class ExportQuestionTemplateService implements ExportQuestionTemplateUseCase {

    private final ExcelFormatterPort excelFormatterPort;

    @Override
    public byte[] execute () {
        return excelFormatterPort.generateTemplate();
    }
}