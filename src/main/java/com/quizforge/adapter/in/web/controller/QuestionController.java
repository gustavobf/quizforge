package com.quizforge.adapter.in.web.controller;

import com.quizforge.adapter.in.web.docs.*;
import com.quizforge.adapter.in.web.dto.response.*;
import com.quizforge.application.port.in.*;
import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.*;

@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
public class QuestionController implements QuestionControllerDocs {

    private final ImportQuestionUseCase importQuestionUseCase;

    @Override
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImportQuestionsResponse> importQuestions (@RequestParam("file") MultipartFile file,
                                                                    @RequestParam("subjectId") Long subjectId) {
        return ResponseEntity.ok(importQuestionUseCase.execute(file, subjectId));
    }
}