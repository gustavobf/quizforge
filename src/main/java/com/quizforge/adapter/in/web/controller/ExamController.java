package com.quizforge.adapter.in.web.controller;

import com.quizforge.adapter.in.web.docs.*;
import com.quizforge.adapter.in.web.dto.request.*;
import com.quizforge.adapter.in.web.dto.response.*;
import com.quizforge.application.port.in.*;
import jakarta.validation.*;
import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/exams")
@RequiredArgsConstructor
public class ExamController implements ExamControllerDocs {

    private final CreateExamUseCase createExamUseCase;
    private final GetCurrentQuestionUseCase getCurrentQuestionUseCase;
    private final AnswerQuestionUseCase answerQuestionUseCase;
    private final FinishExamUseCase finishExamUseCase;

    @PostMapping
    public ResponseEntity<ExamResponse> createExam (@Valid @RequestBody CreateExamRequest request) {
        ExamResponse response = createExamUseCase.execute(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{examId}/questions/current")
    public ResponseEntity<ExamQuestionResponse> getCurrentQuestion (@PathVariable Long examId) {
        ExamQuestionResponse response = getCurrentQuestionUseCase.execute(examId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{examId}/questions/answer")
    public ResponseEntity<Void> answerQuestion (@PathVariable Long examId,
                                                @Valid @RequestBody AnswerQuestionRequest request) {
        answerQuestionUseCase.execute(examId, request.getAlternativeId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{examId}/finish")
    public ResponseEntity<ExamResultResponse> finishExam (@PathVariable Long examId) {
        ExamResultResponse response = finishExamUseCase.execute(examId);
        return ResponseEntity.ok(response);
    }
}