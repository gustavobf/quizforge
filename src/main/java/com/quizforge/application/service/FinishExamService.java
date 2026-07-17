package com.quizforge.application.service;

import com.quizforge.adapter.in.web.dto.request.*;
import com.quizforge.adapter.in.web.dto.response.*;
import com.quizforge.application.port.in.*;
import com.quizforge.application.port.out.*;
import com.quizforge.domain.enumtype.*;
import com.quizforge.domain.exception.*;
import com.quizforge.domain.model.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.time.*;
import java.util.*;
import java.util.stream.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinishExamService implements FinishExamUseCase {

    private final ExamRepositoryPort examRepository;
    private final UserAnswerRepositoryPort userAnswerRepository;

    @Override
    @Transactional
    public ExamResultResponse execute (Long examId, FinishExamRequest request) {
        Exam exam = examRepository.findById(examId).orElseThrow(() -> new ExamNotFoundException(examId));

        if (exam.getFinishedAt() != null) {
            throw new BusinessException("This exam has already been finished");
        }

        if (exam.getStartedAt() == null) {
            throw new BusinessException("Exam has not been started yet");
        }

        List<UserAnswer> userAnswers = processUserAnswers(exam, request.getAnswers());

        userAnswerRepository.saveAll(userAnswers);

        long correctAnswers = userAnswers.stream().filter(UserAnswer::isCorrect).count();

        double score = (correctAnswers * 100.0) / exam.getTotalQuestions();

        exam.finish(score);

        exam = examRepository.save(exam);

        return buildExamResult(exam, userAnswers);
    }

    private void validateAnswer (Question question, List<Long> selectedAlternativeIds) {

        if (question.getType() == QuestionType.SINGLE_CHOICE && selectedAlternativeIds.size() > 1) {

            throw new BusinessException("Question " + question.getId() + " accepts only one alternative");
        }
    }

    private void validateSelectedAlternatives (Question question, List<Long> selectedAlternativeIds) {

        Set<Long> validIds = question.getAlternatives().stream().map(Alternative::getId).collect(Collectors.toSet());

        boolean invalidAlternative = selectedAlternativeIds.stream().anyMatch(id -> !validIds.contains(id));

        if (invalidAlternative) {
            throw new BusinessException("Question " + question.getId() + " contains invalid alternatives");
        }
    }

    private List<UserAnswer> processUserAnswers (Exam exam, Map<Long, List<Long>> answersMap) {

        List<UserAnswer> userAnswers = new ArrayList<>();

        for (ExamQuestion examQuestion : exam.getQuestions()) {

            Question question = examQuestion.getQuestion();

            List<Long> selectedAlternativeIds = answersMap.getOrDefault(question.getId(), Collections.emptyList());

            validateAnswer(question, selectedAlternativeIds);
            validateSelectedAlternatives(question, selectedAlternativeIds);

            boolean isCorrect = question.isCorrectAnswer(selectedAlternativeIds);

            UserAnswer userAnswer = UserAnswer.builder().examId(exam.getId()).questionId(question.getId())
                    .alternativeIds(selectedAlternativeIds).correct(isCorrect).answeredAt(LocalDateTime.now()).build();

            userAnswers.add(userAnswer);
        }

        return userAnswers;
    }

    private ExamResultResponse buildExamResult (Exam exam, List<UserAnswer> userAnswers) {
        long correctCount = userAnswers.stream().filter(UserAnswer::isCorrect).count();
        long wrongCount = userAnswers.stream().filter(ua -> !ua.isCorrect()).count();

        List<ExamResultResponse.QuestionResultDto> questionResults = new ArrayList<>();

        for (ExamQuestion examQuestion : exam.getQuestions()) {
            Question question = examQuestion.getQuestion();

            UserAnswer userAnswer = userAnswers.stream().filter(ua -> ua.getQuestionId().equals(question.getId()))
                    .findFirst().orElse(null);

            List<String> yourAnswer = getYourAnswerList(question, userAnswer);
            List<String> correctAnswer = getCorrectAnswerList(question);
            boolean isCorrect = userAnswer != null && userAnswer.isCorrect();

            questionResults.add(ExamResultResponse.QuestionResultDto.builder().number(examQuestion.getOrderNumber())
                    .statement(question.getStatement()).yourAnswer(yourAnswer).correctAnswer(correctAnswer)
                    .isCorrect(isCorrect).questionType(question.getType().getDescription()).build());
        }

        long timeSpentMinutes = 0;
        if (exam.getStartedAt() != null && exam.getFinishedAt() != null) {
            timeSpentMinutes = Duration.between(exam.getStartedAt(), exam.getFinishedAt()).toMinutes();
        }

        double score = exam.getScore();

        return ExamResultResponse.builder().examId(exam.getId()).title(exam.getTitle())
                .subjectName(exam.getSubject() != null ? exam.getSubject().getName() : null)
                .totalQuestions(exam.getTotalQuestions()).correctAnswers((int) correctCount)
                .wrongAnswers((int) wrongCount).score(score).startedAt(exam.getStartedAt())
                .finishedAt(exam.getFinishedAt()).timeSpentInMinutes((int) timeSpentMinutes).questions(questionResults)
                .build();
    }

    private List<String> getYourAnswerList (Question question, UserAnswer userAnswer) {
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

        return question.getAlternatives().stream().filter(Alternative::isCorrect).map(Alternative::getDescription)
                .collect(Collectors.toList());
    }

    private String buildYourAnswerString (Question question, UserAnswer userAnswer) {
        List<String> answers = getYourAnswerList(question, userAnswer);
        return String.join(", ", answers);
    }

    private String buildCorrectAnswerString (Question question) {
        List<String> answers = getCorrectAnswerList(question);
        return String.join(", ", answers);
    }
}