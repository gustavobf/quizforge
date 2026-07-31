package com.quizforge.application.service;

import com.quizforge.adapter.in.web.dto.request.*;
import com.quizforge.adapter.in.web.dto.response.*;
import com.quizforge.application.port.in.*;
import com.quizforge.application.port.out.*;
import com.quizforge.domain.enumtype.*;
import com.quizforge.domain.exception.*;
import com.quizforge.domain.model.*;
import lombok.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.util.*;
import java.util.stream.*;

@Service
@RequiredArgsConstructor
public class CreateExamService implements CreateExamUseCase {

    private final QuestionRepositoryPort questionRepository;
    private final ExamRepositoryPort examRepository;
    private final SubjectRepositoryPort subjectRepository;

    @Override
    @Transactional
    public CreateExamResponse execute (CreateExamRequest request) {
        if (request.getQuantity() <= 0) {
            throw new BusinessException("Quantity must be greater than zero");
        }

        Subject subject = null;
        if (request.getSubjectId() != null) {
            subject = subjectRepository.findById(request.getSubjectId())
                    .orElseThrow(() -> new SubjectNotFoundException(request.getSubjectId()));
        }

        List<Question> questions;
        if (subject != null) {
            questions = questionRepository.findRandomQuestionsBySubject(request.getQuantity(), subject.getId());
        } else {
            questions = questionRepository.findRandomQuestions(request.getQuantity());
        }

        if (questions.isEmpty()) {
            throw new BusinessException("No questions available for the selected subject");
        }

        if (questions.size() < request.getQuantity()) {
            throw new BusinessException("Not enough questions available. Available: " + questions.size());
        }

        List<ExamQuestion> examQuestions = IntStream.range(0, questions.size())
                .mapToObj(index -> ExamQuestion.builder().question(questions.get(index)).orderNumber(index + 1).build())
                .toList();

        Exam exam = Exam.builder().title(request.getTitle()).subject(subject).questions(examQuestions)
                .totalQuestions(examQuestions.size()).build();

        exam.start();

        exam = examRepository.save(exam);

        List<CreateExamQuestionResponse> createExamQuestionRespons = buildQuestionResponses(examQuestions);

        return CreateExamResponse.builder().examId(exam.getId()).title(exam.getTitle())
                .totalQuestions(exam.getTotalQuestions()).subjectName(subject != null ? subject.getName() : null)
                .questions(createExamQuestionRespons).build();
    }

    private List<CreateExamQuestionResponse> buildQuestionResponses(List<ExamQuestion> examQuestions) {
        return examQuestions.stream()
                .map(examQuestion -> {
                    Question question = examQuestion.question();
                    List<CreateExamAlternativeResponse> alternatives = buildAlternativeResponses(
                            question.alternatives());
                    return CreateExamQuestionResponse.builder()
                            .questionId(question.id())
                            .statement(question.statement())
                            .alternatives(alternatives)
                            .orderNumber(examQuestion.orderNumber())
                            .questionType(question.type())
                            .build();
                })
                .toList();
    }

    private List<CreateExamAlternativeResponse> buildAlternativeResponses(List<Alternative> alternatives) {
        return alternatives.stream()
                .map(alternative -> CreateExamAlternativeResponse.builder()
                        .alternativeId(alternative.id())
                        .description(alternative.description())
                        .build())
                .toList();
    }

}