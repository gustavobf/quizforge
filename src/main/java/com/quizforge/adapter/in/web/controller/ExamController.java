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
    private final FinishExamUseCase finishExamUseCase;

    @PostMapping
    public ResponseEntity<CreateExamResponse> createExam (@Valid @RequestBody CreateExamRequest request) {
        CreateExamResponse response = createExamUseCase.execute(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{examId}/finish")
    public ResponseEntity<ExamResultResponse> finishExam (@PathVariable Long examId,
                                                          @RequestBody FinishExamRequest request) {
        ExamResultResponse response = finishExamUseCase.execute(examId, request);
        return ResponseEntity.ok(response);
    }
}