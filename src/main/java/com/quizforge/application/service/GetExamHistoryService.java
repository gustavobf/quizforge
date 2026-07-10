package com.quizforge.application.service;

import com.quizforge.application.port.in.*;
import com.quizforge.application.port.out.*;
import com.quizforge.domain.exception.*;
import com.quizforge.domain.model.*;
import lombok.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.time.*;
import java.util.*;
import java.util.stream.*;

@Service
@RequiredArgsConstructor
public class GetExamHistoryService implements GetExamHistoryUseCase {

    private final ExamRepositoryPort examRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ExamHistory> execute (int page, int size) {
        List<Exam> exams = examRepository.findCompleted(page, size);
        return exams.stream().map(this::toHistory).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ExamHistorySummary getSummary () {
        List<Exam> exams = examRepository.findCompleted(0, Integer.MAX_VALUE);

        if (exams.isEmpty()) {
            return ExamHistorySummary.builder().totalExams(0).totalQuestionsAnswered(0).totalCorrectAnswers(0)
                    .averageScore(0.0).bestScore(0).worstScore(0).recentExams(List.of()).build();
        }

        int totalExams = exams.size();
        int totalQuestionsAnswered = exams.stream().mapToInt(Exam::getTotalQuestions).sum();
        int totalCorrectAnswers = exams.stream().mapToInt(Exam::getCorrectAnswersCount).sum();
        double averageScore = exams.stream().mapToDouble(Exam::getScore).average().orElse(0.0);
        int bestScore = exams.stream().mapToInt(exam -> (int) exam.getScore()).max().orElse(0);
        int worstScore = exams.stream().mapToInt(exam -> (int) exam.getScore()).min().orElse(0);

        List<ExamHistory> recentExams = exams.stream().limit(5).map(this::toHistory).collect(Collectors.toList());

        return ExamHistorySummary.builder().totalExams(totalExams).totalQuestionsAnswered(totalQuestionsAnswered)
                .totalCorrectAnswers(totalCorrectAnswers).averageScore(Math.round(averageScore * 100.0) / 100.0)
                .bestScore(bestScore).worstScore(worstScore).recentExams(recentExams).build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamHistory> getBySubject (Long subjectId) {
        List<Exam> exams = examRepository.findCompletedBySubjectId(subjectId);
        return exams.stream().map(this::toHistory).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ExamHistory getById (Long examId) {
        Exam exam = examRepository.findById(examId).orElseThrow(() -> new ExamNotFoundException(examId));

        return toHistory(exam);
    }

    private ExamHistory toHistory (Exam exam) {
        long timeSpentMinutes = 0;
        if (exam.getStartedAt() != null && exam.getFinishedAt() != null) {
            timeSpentMinutes = Duration.between(exam.getStartedAt(), exam.getFinishedAt()).toMinutes();
        }

        return ExamHistory.builder().id(exam.getId()).title(exam.getTitle())
                .subjectName(exam.getSubject() != null ? exam.getSubject().getName() : "General")
                .totalQuestions(exam.getTotalQuestions()).correctAnswers(exam.getCorrectAnswersCount())
                .wrongAnswers(exam.getWrongAnswersCount()).score(exam.getScore()).status(exam.getStatus())
                .startedAt(exam.getStartedAt()).finishedAt(exam.getFinishedAt())
                .timeSpentInMinutes((int) timeSpentMinutes).build();
    }
}