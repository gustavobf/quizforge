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

    @Override
    @Transactional(readOnly = true)
    public HistorySummaryResponse getSummary () {

        long totalExams = historyRepository.countFinishedExams();
        long totalQuestionsAnswered = historyRepository.countTotalQuestionsAnswered();
        double averageScore = historyRepository.getAverageScore();
        double bestScore = historyRepository.getBestScore();
        double worstScore = historyRepository.getWorstScore();

        List<Exam> recentExams = historyRepository.findRecentExams(5);
        List<HistorySummaryResponse.RecentExamDto> recentExamDtos = recentExams.stream().map(this::toRecentExamDto)
                .collect(Collectors.toList());

        return HistorySummaryResponse.builder().totalExams((int) totalExams)
                .totalQuestionsAnswered(totalQuestionsAnswered).averageScore(averageScore).bestScore(bestScore)
                .worstScore(worstScore).recentExams(recentExamDtos).build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HistoryExamResponse> getExams (Pageable pageable) {
        return historyRepository.findFinishedExams(pageable).map(this::toHistoryExamResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public HistoryExamResponse getExamDetail (Long examId) {

        Exam exam = historyRepository.findById(examId);
        if (exam == null || exam.getFinishedAt() == null) {
            throw new ExamNotFoundException(examId);
        }

        List<UserAnswer> userAnswers = historyRepository.findUserAnswersByExamId(examId);
        List<HistoryExamResponse.QuestionDetailDto> questionDetails = buildQuestionDetails(exam, userAnswers);

        return HistoryExamResponse.builder().examId(exam.getId()).title(exam.getTitle())
                .subjectName(exam.getSubject() != null ? exam.getSubject().getName() : null)
                .totalQuestions(exam.getTotalQuestions()).score(exam.getScore()).startedAt(exam.getStartedAt())
                .finishedAt(exam.getFinishedAt()).timeSpentInMinutes(calculateTimeSpent(exam))
                .questions(questionDetails).build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectStatsResponse> getSubjectStats () {

        List<Map<String, Object>> stats = historyRepository.getSubjectStats();
        List<SubjectStatsResponse> responses = new ArrayList<>();

        for (Map<String, Object> row : stats) {
            SubjectStatsResponse response = SubjectStatsResponse.builder().subjectId((Long) row.get("subjectId"))
                    .subjectName((String) row.get("subjectName"))
                    .totalExams(((Number) row.get("totalExams")).intValue())
                    .totalQuestions(((Number) row.get("totalQuestions")).intValue())
                    .correctAnswers(((Number) row.get("correctAnswers")).intValue())
                    .averageScore(((Number) row.get("averageScore")).doubleValue())
                    .bestScore(((Number) row.get("bestScore")).doubleValue()).build();
            responses.add(response);
        }

        return responses;
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

    private List<HistoryExamResponse.QuestionDetailDto> buildQuestionDetails (Exam exam, List<UserAnswer> userAnswers) {
        if (exam == null || exam.getQuestions() == null) {
            return new ArrayList<>();
        }

        List<HistoryExamResponse.QuestionDetailDto> questionDetails = new ArrayList<>();

        for (ExamQuestion examQuestion : exam.getQuestions()) {
            Question question = examQuestion.getQuestion();
            if (question == null)
                continue;

            UserAnswer userAnswer = userAnswers.stream().filter(ua -> ua.getQuestionId().equals(question.getId()))
                    .findFirst().orElse(null);

            List<String> yourAnswer = getYourAnswerList(question, userAnswer);
            List<String> correctAnswer = getCorrectAnswerList(question);
            boolean isCorrect = userAnswer != null && userAnswer.isCorrect();

            List<HistoryExamResponse.AlternativeDto> alternatives = question.getAlternatives().stream()
                    .map(alt -> HistoryExamResponse.AlternativeDto.builder().alternativeId(alt.getId())
                            .description(alt.getDescription()).isCorrect(alt.isCorrect()).build())
                    .collect(Collectors.toList());

            questionDetails.add(HistoryExamResponse.QuestionDetailDto.builder().number(examQuestion.getOrderNumber())
                    .questionId(question.getId()).statement(question.getStatement()).alternatives(alternatives)
                    .yourAnswer(yourAnswer).correctAnswer(correctAnswer).isCorrect(isCorrect)
                    .questionType(question.getType().getDescription()).build());
        }

        return questionDetails;
    }

    private List<String> getYourAnswerList (Question question, UserAnswer userAnswer) {
        if (question == null)
            return List.of("Not answered");
        if (userAnswer == null || userAnswer.getAlternativeIds() == null || userAnswer.getAlternativeIds().isEmpty()) {
            return List.of("Not answered");
        }

        return question.getAlternatives().stream().filter(alt -> userAnswer.getAlternativeIds().contains(alt.getId()))
                .map(Alternative::getDescription).collect(Collectors.toList());
    }

    private List<String> getCorrectAnswerList (Question question) {
        if (question == null || question.getAlternatives() == null) {
            return List.of("No correct answer");
        }

        List<String> correctDescriptions = question.getAlternatives().stream().filter(Alternative::isCorrect)
                .map(Alternative::getDescription).collect(Collectors.toList());

        return correctDescriptions.isEmpty() ? List.of("No correct answer") : correctDescriptions;
    }

    private int calculateTimeSpent (Exam exam) {
        if (exam == null || exam.getStartedAt() == null || exam.getFinishedAt() == null) {
            return 0;
        }
        return (int) Duration.between(exam.getStartedAt(), exam.getFinishedAt()).toMinutes();
    }
}