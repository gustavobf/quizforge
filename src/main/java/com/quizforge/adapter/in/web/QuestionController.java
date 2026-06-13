package com.quizforge.adapter.in.web;

import com.quizforge.adapter.in.web.dto.request.*;
import com.quizforge.adapter.in.web.dto.response.*;
import com.quizforge.application.port.in.*;
import com.quizforge.application.usecase.*;
import jakarta.validation.*;
import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final CreateQuestionUseCase createQuestionUseCase;

    @PostMapping
    public ResponseEntity<QuestionResponse> create (@Valid @RequestBody CreateQuestionRequest request) {
        return ResponseEntity.ok(createQuestionUseCase.execute(request));
    }
}