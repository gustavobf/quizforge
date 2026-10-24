package com.quizforge.adapter.in.web.controller;

import com.quizforge.adapter.in.web.docs.*;
import com.quizforge.application.port.in.*;
import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/template")
@RequiredArgsConstructor
public class TemplateController implements TemplateControllerDocs {

    private final ExportQuestionTemplateUseCase exportQuestionTemplateUseCase;

    @GetMapping
    public ResponseEntity<byte[]> downloadTemplate () {

        byte[] file = exportQuestionTemplateUseCase.execute();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=questions-template.xlsx").contentType(
                        MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file);
    }

}