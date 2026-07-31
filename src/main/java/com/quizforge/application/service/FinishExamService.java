package com.quizforge.application.service;

import com.quizforge.adapter.in.web.dto.request.FinishExamRequest;
import com.quizforge.adapter.in.web.dto.response.ExamResultResponse;
import com.quizforge.application.port.in.FinishExamUseCase;
import com.quizforge.application.port.out.CurrentUserPort;
import com.quizforge.application.port.out.ExamRepositoryPort;
import com.quizforge.application.port.out.UserAnswerRepositoryPort;
import com.quizforge.domain.enumtype.QuestionType;
import com.quizforge.domain.exception.BusinessException;
import com.quizforge.domain.exception.ExamNotFoundException;
import com.quizforge.domain.model.Alternative;
import com.quizforge.domain.model.Exam;
import com.quizforge.domain.model.ExamQuestion;
import com.quizforge.domain.model.Question;
import com.quizforge.domain.model.UserAnswer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinishExamService implements FinishExamUseCase {

    private final ExamRepositoryPort examRepository;
    private final UserAnswerRepositoryPort userAnswerRepository;
    private final CurrentUserPort currentUserPort;

    @Override
    @Transactional
    public ExamResultResponse execute(Long examId, FinishExamRequest request) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ExamNotFoundException(examId));
        validateExamOwner(exam);

        validateExamState(exam);

        List<UserAnswer> userAnswers = processUserAnswers(exam, request.getAnswers());
        userAnswerRepository.saveAll(userAnswers);

        long correctAnswers = countCorrectAnswers(userAnswers);
        double score = calculateScore(correctAnswers, exam.getTotalQuestions());

        exam.finish(score);
        exam = examRepository.save(exam);

        return buildExamResult(exam, userAnswers);
    }

    private void validateExamState(Exam exam) {
        if (exam.getFinishedAt() != null) {
            throw new BusinessException("This exam has already been finished");
        }
        if (exam.getStartedAt() == null) {
            throw new BusinessException("Exam has not been started yet");
        }
    }

    private void validateExamOwner(Exam exam) {
        Long currentUserId = currentUserPort.requireCurrentUserId();
        if (!currentUserPort.isAdmin() && !Objects.equals(exam.getOwnerId(), currentUserId)) {
            throw new BusinessException("You cannot access another user's exam");
        }
    }

    private List<UserAnswer> processUserAnswers(Exam exam, Map<Long, List<Long>> answersMap) {
        return exam.getQuestions().stream()
                .map(examQuestion -> {
                    Question question = examQuestion.question();
                    List<Long> selectedAlternativeIds = answersMap.getOrDefault(question.id(), Collections.emptyList());

                    validateAnswer(question, selectedAlternativeIds);
                    validateSelectedAlternatives(question, selectedAlternativeIds);

                    boolean isCorrect = question.isCorrectAnswer(selectedAlternativeIds);

                    return UserAnswer.builder()
                            .examId(exam.getId())
                            .questionId(question.id())
                            .alternativeIds(selectedAlternativeIds)
                            .correct(isCorrect)
                            .answeredAt(LocalDateTime.now())
                            .build();
                })
                .toList();
    }

    private void validateAnswer(Question question, List<Long> selectedAlternativeIds) {
        if (question.type() == QuestionType.SINGLE_CHOICE && selectedAlternativeIds.size() > 1) {
            throw new BusinessException("Question " + question.id() + " accepts only one alternative");
        }
    }

    private void validateSelectedAlternatives(Question question, List<Long> selectedAlternativeIds) {
        Set<Long> validIds = question.alternatives().stream()
                .map(Alternative::id)
                .collect(Collectors.toSet());

        boolean hasInvalidAlternative = selectedAlternativeIds.stream()
                .anyMatch(id -> !validIds.contains(id));

        if (hasInvalidAlternative) {
            throw new BusinessException("Question " + question.id() + " contains invalid alternatives");
        }
    }

    private long countCorrectAnswers(List<UserAnswer> userAnswers) {
        return userAnswers.stream()
                .filter(UserAnswer::correct)
                .count();
    }

    private double calculateScore(long correctAnswers, int totalQuestions) {
        return totalQuestions == 0 ? 0 : (correctAnswers * 100.0) / totalQuestions;
    }

    private ExamResultResponse buildExamResult(Exam exam, List<UserAnswer> userAnswers) {
        Map<Long, UserAnswer> userAnswerMap = userAnswers.stream()
                .collect(Collectors.toMap(UserAnswer::questionId, ua -> ua));

        long correctCount = countCorrectAnswers(userAnswers);
        long wrongCount = userAnswers.size() - correctCount;

        List<ExamResultResponse.QuestionResultDto> questionResults = exam.getQuestions().stream()
                .map(examQuestion -> buildQuestionResult(examQuestion, userAnswerMap))
                .toList();

        long timeSpentMinutes = calculateTimeSpent(exam);

        return ExamResultResponse.builder()
                .examId(exam.getId())
                .title(exam.getTitle())
                .subjectName(exam.getSubject() != null ? exam.getSubject().getName() : null)
                .totalQuestions(exam.getTotalQuestions())
                .correctAnswers((int) correctCount)
                .wrongAnswers((int) wrongCount)
                .score(exam.getScore())
                .startedAt(exam.getStartedAt())
                .finishedAt(exam.getFinishedAt())
                .timeSpentInMinutes((int) timeSpentMinutes)
                .questions(questionResults)
                .build();
    }

    private ExamResultResponse.QuestionResultDto buildQuestionResult(
            ExamQuestion examQuestion,
            Map<Long, UserAnswer> userAnswerMap) {
        Question question = examQuestion.question();
        UserAnswer userAnswer = userAnswerMap.get(question.id());

        List<String> yourAnswers = getYourAnswerList(question, userAnswer);
        List<String> correctAnswers = getCorrectAnswerList(question);
        boolean isCorrect = userAnswer != null && userAnswer.correct();

        return ExamResultResponse.QuestionResultDto.builder()
                .number(examQuestion.orderNumber())
                .statement(question.statement())
                .yourAnswer(yourAnswers)
                .correctAnswer(correctAnswers)
                .isCorrect(isCorrect)
                .questionType(question.type().getDescription())
                .build();
    }

    private List<String> getYourAnswerList(Question question, UserAnswer userAnswer) {
        if (userAnswer == null || userAnswer.alternativeIds() == null || userAnswer.alternativeIds().isEmpty()) {
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

        return question.alternatives().stream()
                .filter(Alternative::correct)
                .map(Alternative::description)
                .toList();
    }

    private long calculateTimeSpent(Exam exam) {
        if (exam.getStartedAt() != null && exam.getFinishedAt() != null) {
            return Duration.between(exam.getStartedAt(), exam.getFinishedAt()).toMinutes();
        }
        return 0;
    }
}