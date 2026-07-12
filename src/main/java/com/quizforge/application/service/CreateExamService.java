package com.quizforge.application.service;

import com.quizforge.adapter.in.web.dto.request.*;
import com.quizforge.adapter.in.web.dto.response.*;
import com.quizforge.application.port.in.*;
import com.quizforge.application.port.out.*;
import com.quizforge.domain.exception.*;
import com.quizforge.domain.model.*;
import lombok.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import java.util.*;

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

        Exam exam = Exam.builder().title(request.getTitle()).subject(subject).totalQuestions(questions.size()).build();

        List<ExamQuestion> examQuestions = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            ExamQuestion examQuestion = ExamQuestion.builder().question(questions.get(i)).orderNumber(i + 1)
                    .answered(false).correct(false).build();
            examQuestions.add(examQuestion);
        }

        exam = Exam.builder().title(exam.getTitle()).subject(exam.getSubject()).questions(examQuestions)
                .totalQuestions(examQuestions.size()).build();

        exam.start();
        exam = examRepository.save(exam);

        List<CreateExamQuestionResponse> createExamQuestionRespons = buildQuestionResponses(examQuestions);

        return CreateExamResponse.builder().examId(exam.getId()).title(exam.getTitle())
                .totalQuestions(exam.getTotalQuestions()).subjectName(subject != null ? subject.getName() : null)
                .questions(createExamQuestionRespons).build();
    }

    private List<CreateExamQuestionResponse> buildQuestionResponses (List<ExamQuestion> examQuestions) {
        List<CreateExamQuestionResponse> createExamQuestionRespons = new ArrayList<>();

        for (ExamQuestion examQuestion : examQuestions) {
            Question question = examQuestion.getQuestion();

            List<CreateExamAlternativeResponse> createExamAlternativeRespons = buildAlternativeResponses(
                    question.getAlternatives());

            CreateExamQuestionResponse createExamQuestionResponse = CreateExamQuestionResponse.builder()
                    .questionId(question.getId()).statement(question.getStatement())
                    .alternatives(createExamAlternativeRespons).orderNumber(examQuestion.getOrderNumber()).build();

            createExamQuestionRespons.add(createExamQuestionResponse);
        }

        return createExamQuestionRespons;
    }

    private List<CreateExamAlternativeResponse> buildAlternativeResponses (List<Alternative> alternatives) {
        List<CreateExamAlternativeResponse> createExamAlternativeRespons = new ArrayList<>();

        for (Alternative alternative : alternatives) {
            CreateExamAlternativeResponse createExamAlternativeResponse = CreateExamAlternativeResponse.builder()
                    .alternativeId(alternative.getId()).description(alternative.getDescription()).build();
            createExamAlternativeRespons.add(createExamAlternativeResponse);
        }

        return createExamAlternativeRespons;
    }
}