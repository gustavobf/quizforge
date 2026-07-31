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
public class CreateQuestionService implements CreateQuestionUseCase {

    private final QuestionRepositoryPort questionRepository;
    private final SubjectRepositoryPort subjectRepository;

    @Override
    @Transactional
    public CreateQuestionResponse execute(CreateQuestionRequest request) {
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new SubjectNotFoundException(request.getSubjectId()));

        List<Alternative> alternatives = request.getAlternatives().stream()
                .map(alt -> Alternative.builder().description(alt.getDescription()).correct(alt.getCorrect()).build())
                .toList();

        Question question = Question.builder()
                .statement(request.getStatement())
                .subject(subject)
                .type(determineQuestionType(alternatives))
                .alternatives(alternatives)
                .build();

        Question saved = questionRepository.save(question);

        return CreateQuestionResponse.builder()
                .id(saved.id())
                .statement(saved.statement())
                .subjectId(saved.subject().getId())
                .subjectName(saved.subject().getName())
                .alternatives(saved.alternatives().stream()
                        .map(alt -> CreateQuestionAlternativeDto.builder()
                                .id(alt.id())
                                .statement(alt.description())
                                .build())
                        .toList())
                .build();
    }

    private QuestionType determineQuestionType (List<Alternative> alternatives) {

        long correctCount = alternatives.stream().filter(Alternative::correct).count();

        if (correctCount == 0) {
            throw new BusinessException("Question must have at least one correct alternative");
        }

        return correctCount > 1 ? QuestionType.MULTIPLE_CHOICE : QuestionType.SINGLE_CHOICE;
    }
}