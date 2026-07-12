package com.quizforge.application.service;

import com.quizforge.adapter.in.web.dto.request.*;
import com.quizforge.adapter.in.web.dto.response.*;
import com.quizforge.application.port.in.*;
import com.quizforge.application.port.out.*;
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

        exam.finish();
        exam = examRepository.save(exam);

        return buildExamResult(exam, userAnswers);
    }

    private List<UserAnswer> processUserAnswers (Exam exam, Map<Long, List<Long>> answersMap) {
        List<UserAnswer> userAnswers = new ArrayList<>();

        for (ExamQuestion examQuestion : exam.getQuestions()) {
            Question question = examQuestion.getQuestion();
            Long questionId = question.getId();

            List<Long> selectedAlternativeIds = answersMap.getOrDefault(questionId, new ArrayList<>());

            validateAlternatives(question, selectedAlternativeIds);

            boolean isCorrect = checkAnswer(question, selectedAlternativeIds);

            examQuestion.setAnswered(true);
            examQuestion.setCorrect(isCorrect);
            examQuestion.setSelectedAlternativeIds(selectedAlternativeIds);

            UserAnswer userAnswer = UserAnswer.builder().examId(exam.getId()).questionId(questionId)
                    .alternativeIds(selectedAlternativeIds).correct(isCorrect).answeredAt(LocalDateTime.now()).build();

            userAnswers.add(userAnswer);
        }

        return userAnswers;
    }

    private void validateAlternatives (Question question, List<Long> selectedAlternativeIds) {
        if (selectedAlternativeIds == null || selectedAlternativeIds.isEmpty()) {
            return;
        }

        Set<Long> validAlternativeIds = question.getAlternatives().stream().map(Alternative::getId)
                .collect(Collectors.toSet());

        for (Long selectedId : selectedAlternativeIds) {
            if (!validAlternativeIds.contains(selectedId)) {
                throw new BusinessException("Invalid alternative ID: " + selectedId);
            }
        }
    }

    private boolean checkAnswer (Question question, List<Long> selectedAlternativeIds) {
        if (selectedAlternativeIds == null || selectedAlternativeIds.isEmpty()) {
            return false;
        }

        Set<Long> correctAlternativeIds = question.getAlternatives().stream().filter(Alternative::isCorrect)
                .map(Alternative::getId).collect(Collectors.toSet());

        if (question.isMultipleChoice()) {
            return new HashSet<>(selectedAlternativeIds).equals(correctAlternativeIds);
        }

        return selectedAlternativeIds.size() == 1 && correctAlternativeIds.contains(selectedAlternativeIds.get(0));
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
            String questionType = question.isMultipleChoice() ? "MULTIPLE_CHOICE" : "SINGLE_CHOICE";

            questionResults.add(ExamResultResponse.QuestionResultDto.builder().number(examQuestion.getOrderNumber())
                    .statement(question.getStatement()).yourAnswer(yourAnswer).correctAnswer(correctAnswer)
                    .isCorrect(isCorrect).questionType(questionType).build());
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