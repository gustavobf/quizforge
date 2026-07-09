package com.quizforge.adapter.in.web.mapper;

import com.quizforge.adapter.in.web.dto.request.*;
import com.quizforge.adapter.in.web.dto.response.*;
import com.quizforge.domain.model.*;
import org.springframework.stereotype.*;

import java.util.*;

@Component
public class QuestionWebMapper {

    public Question toDomain (CreateQuestionRequest request, Subject subject) {
        List<Alternative> alternatives = request.getAlternatives().stream().map(this::toAlternativeDomain).toList();

        return Question.builder().statement(request.getStatement()).subject(subject).alternatives(alternatives).build();
    }

    public Alternative toAlternativeDomain (CreateAlternativeRequest request) {
        return Alternative.builder().description(request.getDescription()).correct(request.getCorrect()).build();
    }

    public QuestionResponse toResponse (Question question) {
        List<QuestionResponse.AlternativeDto> alternatives = question.getAlternatives().stream()
                .map(this::toAlternativeDto).toList();

        return QuestionResponse.builder().id(question.getId()).statement(question.getStatement())
                .subjectId(question.getSubject() != null ? question.getSubject().getId() : null)
                .subjectName(question.getSubject() != null ? question.getSubject().getName() : null)
                .alternatives(alternatives).build();
    }

    public QuestionResponse.AlternativeDto toAlternativeDto (Alternative alternative) {
        return QuestionResponse.AlternativeDto.builder().id(alternative.getId()).statement(alternative.getDescription())
                .correct(alternative.isCorrect()).build();
    }
}