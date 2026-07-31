package com.quizforge.application.service;

import com.quizforge.adapter.in.web.dto.response.*;
import com.quizforge.application.port.in.*;
import com.quizforge.application.port.out.*;
import com.quizforge.domain.exception.*;
import com.quizforge.domain.model.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.time.*;
import java.util.*;
import java.util.stream.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetHistoryService implements GetHistoryUseCase {

    private final HistoryRepositoryPort historyRepository;
    private final CurrentUserPort currentUserPort;

    @Override
    @Transactional(readOnly = true)
    public HistorySummaryResponse getSummary() {
        Long userId = currentUserPort.requireCurrentUserId();
        long totalExams = historyRepository.countFinishedExams(userId);
        long totalQuestionsAnswered = historyRepository.countTotalQuestionsAnswered(userId);
        double averageScore = historyRepository.getAverageScore(userId);
        double bestScore = historyRepository.getBestScore(userId);
        double worstScore = historyRepository.getWorstScore(userId);

        List<HistorySummaryResponse.RecentExamDto> recentExamDtos = historyRepository.findRecentExams(userId, 5).stream()
                .map(this::toRecentExamDto)
                .toList();

        return HistorySummaryResponse.builder()
                .totalExams((int) totalExams)
                .totalQuestionsAnswered(totalQuestionsAnswered)
                .averageScore(averageScore)
                .bestScore(bestScore)
                .worstScore(worstScore)
                .recentExams(recentExamDtos)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HistoryExamResponse> getExams (Pageable pageable) {
        return historyRepository.findFinishedExams(currentUserPort.requireCurrentUserId(), pageable).map(this::toHistoryExamResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public HistoryExamResponse getExamDetail (Long examId) {

        Long userId = currentUserPort.requireCurrentUserId();
        Exam exam = historyRepository.findById(userId, examId);
        if (exam == null || exam.getFinishedAt() == null) {
            throw new ExamNotFoundException(examId);
        }

        List<UserAnswer> userAnswers = historyRepository.findUserAnswersByExamId(userId, examId);
        List<HistoryExamResponse.QuestionDetailDto> questionDetails = buildQuestionDetails(exam, userAnswers);

        return HistoryExamResponse.builder().examId(exam.getId()).title(exam.getTitle())
                .subjectName(exam.getSubject() != null ? exam.getSubject().getName() : null)
                .totalQuestions(exam.getTotalQuestions()).score(exam.getScore()).startedAt(exam.getStartedAt())
                .finishedAt(exam.getFinishedAt()).timeSpentInMinutes(calculateTimeSpent(exam))
                .questions(questionDetails).build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectStatsResponse> getSubjectStats() {
        return historyRepository.getSubjectStats(currentUserPort.requireCurrentUserId()).stream()
                .map(row -> SubjectStatsResponse.builder()
                        .subjectId((Long) row.get("subjectId"))
                        .subjectName((String) row.get("subjectName"))
                        .totalExams(((Number) row.get("totalExams")).intValue())
                        .totalQuestions(((Number) row.get("totalQuestions")).intValue())
                        .correctAnswers(((Number) row.get("correctAnswers")).intValue())
                        .averageScore(((Number) row.get("averageScore")).doubleValue())
                        .bestScore(((Number) row.get("bestScore")).doubleValue())
                        .build())
                .toList();
    }

    private HistorySummaryResponse.RecentExamDto toRecentExamDto (Exam exam) {
        if (exam == null)
            return null;

        return HistorySummaryResponse.RecentExamDto.builder().id(exam.getId()).title(exam.getTitle())
                .subjectName(exam.getSubject() != null ? exam.getSubject().getName() : null).score(exam.getScore())
                .totalQuestions(exam.getTotalQuestions())
                .finishedAt(exam.getFinishedAt() != null ? exam.getFinishedAt().toString() : null).build();
    }

    private HistoryExamResponse toHistoryExamResponse (Exam exam) {
        if (exam == null)
            return null;

        return HistoryExamResponse.builder().examId(exam.getId()).title(exam.getTitle())
                .subjectName(exam.getSubject() != null ? exam.getSubject().getName() : null)
                .totalQuestions(exam.getTotalQuestions()).score(exam.getScore()).startedAt(exam.getStartedAt())
                .finishedAt(exam.getFinishedAt()).timeSpentInMinutes(calculateTimeSpent(exam)).questions(null).build();
    }

    private List<HistoryExamResponse.QuestionDetailDto> buildQuestionDetails(Exam exam, List<UserAnswer> userAnswers) {
        if (exam == null || exam.getQuestions() == null) {
            return List.of();
        }

        return exam.getQuestions().stream()
                .map(examQuestion -> {
                    Question question = examQuestion.question();
                    if (question == null) return null;

                    UserAnswer userAnswer = userAnswers.stream()
                            .filter(ua -> ua.questionId().equals(question.id()))
                            .findFirst()
                            .orElse(null);

                    List<String> yourAnswer = getYourAnswerList(question, userAnswer);
                    List<String> correctAnswer = getCorrectAnswerList(question);
                    boolean isCorrect = userAnswer != null && userAnswer.correct();

                    List<HistoryExamResponse.AlternativeDto> alternatives = question.alternatives().stream()
                            .map(alt -> HistoryExamResponse.AlternativeDto.builder()
                                    .alternativeId(alt.id())
                                    .description(alt.description())
                                    .isCorrect(alt.correct())
                                    .build())
                            .toList();

                    return HistoryExamResponse.QuestionDetailDto.builder()
                            .number(examQuestion.orderNumber())
                            .questionId(question.id())
                            .statement(question.statement())
                            .alternatives(alternatives)
                            .yourAnswer(yourAnswer)
                            .correctAnswer(correctAnswer)
                            .isCorrect(isCorrect)
                            .questionType(question.type().getDescription())
                            .build();
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private List<String> getYourAnswerList(Question question, UserAnswer userAnswer) {
        if (question == null || userAnswer == null || userAnswer.alternativeIds() == null || userAnswer.alternativeIds().isEmpty()) {
            return List.of("Not answered");
        }

        return question.alternatives().stream()
                .filter(alt -> userAnswer.alternativeIds().contains(alt.id()))
                .map(Alternative::description)
                .toList();
    }

    private List<String> getCorrectAnswerList(Question question) {
        if (question == null || question.alternatives() == null) {
            return List.of("No correct answer");
        }

        List<String> correctDescriptions = question.alternatives().stream()
                .filter(Alternative::correct)
                .map(Alternative::description)
                .toList();

        return correctDescriptions.isEmpty() ? List.of("No correct answer") : correctDescriptions;
    }

    private int calculateTimeSpent (Exam exam) {
        if (exam == null || exam.getStartedAt() == null || exam.getFinishedAt() == null) {
            return 0;
        }
        return (int) Duration.between(exam.getStartedAt(), exam.getFinishedAt()).toMinutes();
    }
}