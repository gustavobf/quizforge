package com.quizforge.adapter.in.web.controller;

import com.quizforge.adapter.in.web.docs.*;
import com.quizforge.adapter.in.web.dto.response.*;
import com.quizforge.application.port.in.*;
import com.quizforge.domain.model.*;
import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.*;

@RestController
@RequestMapping("/history")
@RequiredArgsConstructor
public class HistoryController implements HistoryControllerDocs {

    private final GetExamHistoryUseCase getExamHistoryUseCase;

    @Override
    @GetMapping
    public ResponseEntity<List<ExamHistoryResponse>> getHistory (@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "20") int size) {
        List<ExamHistory> history = getExamHistoryUseCase.execute(page, size);

        List<ExamHistoryResponse> responses = history.stream().map(this::toResponse).collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @Override
    @GetMapping("/summary")
    public ResponseEntity<ExamHistorySummaryResponse> getSummary () {
        ExamHistorySummary summary = getExamHistoryUseCase.getSummary();

        return ResponseEntity.ok(toSummaryResponse(summary));
    }

    @Override
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<ExamHistoryResponse>> getBySubject (@PathVariable Long subjectId) {
        List<ExamHistory> history = getExamHistoryUseCase.getBySubject(subjectId);

        List<ExamHistoryResponse> responses = history.stream().map(this::toResponse).collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @Override
    @GetMapping("/{examId}")
    public ResponseEntity<ExamHistoryResponse> getById (@PathVariable Long examId) {
        ExamHistory history = getExamHistoryUseCase.getById(examId);

        return ResponseEntity.ok(toResponse(history));
    }

    private ExamHistoryResponse toResponse (ExamHistory history) {
        return ExamHistoryResponse.builder().id(history.getId()).title(history.getTitle())
                .subjectName(history.getSubjectName()).totalQuestions(history.getTotalQuestions())
                .correctAnswers(history.getCorrectAnswers()).wrongAnswers(history.getWrongAnswers())
                .score(history.getScore()).status(history.getStatus()).startedAt(history.getStartedAt())
                .finishedAt(history.getFinishedAt()).timeSpentInMinutes(history.getTimeSpentInMinutes()).build();
    }

    private ExamHistorySummaryResponse toSummaryResponse (ExamHistorySummary summary) {
        List<ExamHistoryResponse> recentResponses = summary.getRecentExams().stream().map(this::toResponse)
                .collect(Collectors.toList());

        return ExamHistorySummaryResponse.builder().totalExams(summary.getTotalExams())
                .totalQuestionsAnswered(summary.getTotalQuestionsAnswered())
                .totalCorrectAnswers(summary.getTotalCorrectAnswers()).averageScore(summary.getAverageScore())
                .bestScore(summary.getBestScore()).worstScore(summary.getWorstScore()).recentExams(recentResponses)
                .build();
    }
}