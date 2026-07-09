package com.quizforge.adapter.in.web.controller;

import com.quizforge.adapter.in.web.docs.QuestionControllerDocs;
import com.quizforge.adapter.in.web.dto.request.CreateQuestionRequest;
import com.quizforge.adapter.in.web.dto.response.ImportQuestionsResponse;
import com.quizforge.adapter.in.web.dto.response.QuestionResponse;
import com.quizforge.application.port.in.CreateQuestionUseCase;
import com.quizforge.application.port.in.ImportQuestionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
public class QuestionController implements QuestionControllerDocs {

    private final CreateQuestionUseCase createQuestionUseCase;
    private final ImportQuestionUseCase importQuestionUseCase;

    @Override
    @PostMapping("/create")
    public ResponseEntity<QuestionResponse> create(@Valid @RequestBody CreateQuestionRequest request) {
        return ResponseEntity.ok(createQuestionUseCase.execute(request));
    }

    @Override
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImportQuestionsResponse> importQuestions(
            @RequestParam("file") MultipartFile file,
            @RequestParam("subjectId") Long subjectId) {
        return ResponseEntity.ok(importQuestionUseCase.execute(file, subjectId));
    }
}