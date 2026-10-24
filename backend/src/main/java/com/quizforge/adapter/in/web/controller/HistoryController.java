package com.quizforge.adapter.in.web.controller;

import com.quizforge.adapter.in.web.docs.*;
import com.quizforge.adapter.in.web.dto.response.*;
import com.quizforge.application.port.in.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/history")
@RequiredArgsConstructor
public class HistoryController implements HistoryControllerDocs {

    private final GetHistoryUseCase getHistoryUseCase;

    @GetMapping("/summary")
    public ResponseEntity<HistorySummaryResponse> getSummary () {
        HistorySummaryResponse response = getHistoryUseCase.getSummary();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/exams")
    public ResponseEntity<Page<HistoryExamResponse>> getExams (@RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size,
                                                               @RequestParam(defaultValue = "finishedAt") String sortBy,
                                                               @RequestParam(defaultValue = "desc") String sortDirection) {


        if (page < 0)
            page = 0;
        if (size < 1)
            size = 1;
        if (size > 100)
            size = 100;

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<HistoryExamResponse> response = getHistoryUseCase.getExams(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/exams/{examId}")
    public ResponseEntity<HistoryExamResponse> getExamDetail (@PathVariable Long examId) {
        HistoryExamResponse response = getHistoryUseCase.getExamDetail(examId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/subjects")
    public ResponseEntity<List<SubjectStatsResponse>> getSubjectStats () {
        List<SubjectStatsResponse> response = getHistoryUseCase.getSubjectStats();
        return ResponseEntity.ok(response);
    }
}