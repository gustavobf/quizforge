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

@Service
@RequiredArgsConstructor
public class CreateExamService implements CreateExamUseCase {

    private final QuestionRepositoryPort questionRepository;
    private final ExamRepositoryPort examRepository;
    private final SubjectRepositoryPort subjectRepository;

    @Override
    @Transactional
    public ExamResponse execute (CreateExamRequest request) {
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

        Exam exam = Exam.builder().title(request.getTitle()).description(request.getDescription()).subject(subject)
                .totalQuestions(questions.size()).currentQuestionIndex(0).status(ExamStatus.NOT_STARTED).build();

        List<ExamQuestion> examQuestions = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            ExamQuestion examQuestion = ExamQuestion.builder().question(questions.get(i)).orderNumber(i + 1)
                    .answered(false).correct(false).build();
            examQuestions.add(examQuestion);
        }

        exam = Exam.builder().title(exam.getTitle()).description(exam.getDescription()).subject(exam.getSubject())
                .questions(examQuestions).totalQuestions(examQuestions.size()).currentQuestionIndex(0)
                .status(ExamStatus.NOT_STARTED).build();

        exam.start();
        exam = examRepository.save(exam);

        return ExamResponse.builder().examId(exam.getId()).title(exam.getTitle())
                .totalQuestions(exam.getTotalQuestions()).subjectName(subject != null ? subject.getName() : null)
                .status(exam.getStatus()).build();
    }
}