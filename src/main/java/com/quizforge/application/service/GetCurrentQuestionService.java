package com.quizforge.application.service;

import com.quizforge.adapter.in.web.dto.response.*;
import com.quizforge.application.port.in.*;
import com.quizforge.application.port.out.*;
import com.quizforge.domain.enumtype.*;
import com.quizforge.domain.exception.*;
import com.quizforge.domain.model.*;
import lombok.*;
import org.springframework.stereotype.*;

import java.util.stream.*;

@Service
@RequiredArgsConstructor
public class GetCurrentQuestionService implements GetCurrentQuestionUseCase {

    private final ExamRepositoryPort examRepository;

    @Override
    public ExamQuestionResponse execute (Long examId) {
        Exam exam = examRepository.findById(examId).orElseThrow(() -> new ExamNotFoundException(examId));

        if (exam.getStatus() != ExamStatus.IN_PROGRESS) {
            throw new BusinessException("Exam is not in progress");
        }

        ExamQuestion currentQuestion = exam.getCurrentQuestion();
        if (currentQuestion == null) {
            throw new BusinessException("No current question available");
        }

        return ExamQuestionResponse.builder().examId(exam.getId()).totalQuestions(exam.getTotalQuestions())
                .currentQuestionNumber(exam.getCurrentQuestionIndex() + 1)
                .statement(currentQuestion.getQuestion().getStatement()).alternatives(
                        currentQuestion.getQuestion().getAlternativesWithoutCorrectAnswer().stream()
                                .map(alt -> ExamQuestionResponse.AlternativeDto.builder().id(alt.getId())
                                        .statement(alt.getDescription()).build()).collect(Collectors.toList()))
                .isAnswered(currentQuestion.isAnswered())
                .selectedAlternativeId(currentQuestion.getSelectedAlternativeId()).build();
    }
}