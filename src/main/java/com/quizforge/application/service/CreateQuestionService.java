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
import java.util.stream.*;

@Service
@RequiredArgsConstructor
public class CreateQuestionService implements CreateQuestionUseCase {

    private final QuestionRepositoryPort questionRepository;
    private final SubjectRepositoryPort subjectRepository;

    @Override
    @Transactional
    public QuestionResponse execute (CreateQuestionRequest request) {
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new SubjectNotFoundException(request.getSubjectId()));

        List<Alternative> alternatives = request.getAlternatives().stream()
                .map(alt -> Alternative.builder().description(alt.getDescription()).correct(alt.getCorrect()).build())
                .collect(Collectors.toList());

        Question question = Question.builder().statement(request.getStatement()).subject(subject)
                .alternatives(alternatives).build();

        Question saved = questionRepository.save(question);

        return QuestionResponse.builder().id(saved.getId()).statement(saved.getStatement())
                .subjectId(saved.getSubject().getId()).subjectName(saved.getSubject().getName()).alternatives(
                        saved.getAlternatives().stream()
                                .map(alt -> QuestionResponse.AlternativeDto.builder().id(alt.getId())
                                        .statement(alt.getDescription()).correct(alt.isCorrect()).build())
                                .collect(Collectors.toList())).build();
    }
}