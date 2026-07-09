package com.quizforge.application.service;

import com.quizforge.adapter.in.web.dto.response.*;
import com.quizforge.application.port.in.*;
import com.quizforge.application.port.out.*;
import com.quizforge.domain.enumtype.*;
import com.quizforge.domain.exception.*;
import com.quizforge.domain.model.*;
import lombok.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FinishExamService implements FinishExamUseCase {

    private final ExamRepositoryPort examRepository;
    private final UserAnswerRepositoryPort userAnswerRepository;

    @Override
    @Transactional
    public ExamResultResponse execute (Long examId) {
        Exam exam = examRepository.findById(examId).orElseThrow(() -> new ExamNotFoundException(examId));

        if (exam.getStatus() == ExamStatus.NOT_STARTED) {
            throw new ExamNotInProgressException(examId);
        }

        if (exam.getStatus() == ExamStatus.IN_PROGRESS && !exam.isComplete()) {
            throw new ExamNotCompleteException(examId, exam.getAnsweredQuestionsCount(), exam.getTotalQuestions());
        }

        if (exam.getStatus() != ExamStatus.COMPLETED) {
            exam.finish();
            examRepository.save(exam);
        }

        List<ExamResultResponse.QuestionResultDto> questionResults = new ArrayList<>();

        for (ExamQuestion examQuestion : exam.getQuestions()) {
            String yourAnswer = "Not answered";
            String correctAnswer = examQuestion.getQuestion().getAlternatives().stream().filter(Alternative::isCorrect)
                    .findFirst().map(Alternative::getDescription).orElse("");

            if (examQuestion.isAnswered()) {
                yourAnswer = examQuestion.getQuestion().getAlternatives().stream()
                        .filter(alt -> alt.getId().equals(examQuestion.getSelectedAlternativeId())).findFirst()
                        .map(Alternative::getDescription).orElse("Not answered");
            }

            questionResults.add(ExamResultResponse.QuestionResultDto.builder().number(examQuestion.getOrderNumber())
                    .statement(examQuestion.getQuestion().getStatement()).yourAnswer(yourAnswer)
                    .correctAnswer(correctAnswer).isCorrect(examQuestion.isCorrect()).build());
        }

        long timeSpentMinutes = 0;
        if (exam.getStartedAt() != null && exam.getFinishedAt() != null) {
            timeSpentMinutes = Duration.between(exam.getStartedAt(), exam.getFinishedAt()).toMinutes();
        }

        return ExamResultResponse.builder().examId(exam.getId()).title(exam.getTitle())
                .totalQuestions(exam.getTotalQuestions()).correctAnswers(exam.getCorrectAnswersCount())
                .wrongAnswers(exam.getWrongAnswersCount()).score(exam.getScore()).status(exam.getStatus())
                .startedAt(exam.getStartedAt()).finishedAt(exam.getFinishedAt())
                .timeSpentInMinutes((int) timeSpentMinutes).questions(questionResults).build();
    }
}