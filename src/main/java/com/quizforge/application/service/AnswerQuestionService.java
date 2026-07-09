package com.quizforge.application.service;

import com.quizforge.application.port.in.*;
import com.quizforge.application.port.out.*;
import com.quizforge.domain.enumtype.*;
import com.quizforge.domain.exception.*;
import com.quizforge.domain.model.*;
import lombok.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.time.*;

@Service
@RequiredArgsConstructor
public class AnswerQuestionService implements AnswerQuestionUseCase {

    private final ExamRepositoryPort examRepository;
    private final UserAnswerRepositoryPort userAnswerRepository;

    @Override
    @Transactional
    public void execute (Long examId, Long alternativeId) {
        Exam exam = examRepository.findById(examId).orElseThrow(() -> new ExamNotFoundException(examId));

        if (exam.getStatus() != ExamStatus.IN_PROGRESS) {
            throw new ExamNotInProgressException(examId);
        }

        ExamQuestion currentQuestion = exam.getCurrentQuestion();
        if (currentQuestion == null) {
            throw new InvalidAnswerException(0L, alternativeId);
        }

        if (currentQuestion.isAnswered()) {
            throw new QuestionAlreadyAnsweredException(currentQuestion.getQuestion().getId());
        }

        boolean alternativeExists = currentQuestion.getQuestion().getAlternatives().stream()
                .anyMatch(alt -> alt.getId().equals(alternativeId));

        if (!alternativeExists) {
            throw new InvalidAnswerException(currentQuestion.getQuestion().getId(), alternativeId);
        }

        currentQuestion.answer(alternativeId);

        UserAnswer userAnswer = UserAnswer.builder().examId(exam.getId())
                .questionId(currentQuestion.getQuestion().getId()).alternativeId(alternativeId)
                .correct(currentQuestion.isCorrect()).answeredAt(LocalDateTime.now()).build();
        userAnswerRepository.save(userAnswer);

        if (exam.getCurrentQuestionIndex() < exam.getTotalQuestions() - 1) {
            exam.nextQuestion();
        }

        examRepository.save(exam);

        if (exam.isComplete()) {
            exam.finish();
            examRepository.save(exam);
        }
    }
}