package com.quizforge.adapter.in.web.mapper;

import com.quizforge.adapter.in.web.dto.request.*;
import com.quizforge.adapter.in.web.dto.response.*;
import com.quizforge.domain.entity.Alternative;
import com.quizforge.domain.entity.Question;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QuestionMapper {

    public Question toDomain (CreateQuestionRequest request) {

        List<Alternative> alternatives = request.alternatives().stream().map(this::toDomain).toList();

        return Question.builder().statement(request.statement()).alternatives(alternatives).build();
    }

    public Alternative toDomain (CreateAlternativeRequest request) {

        return Alternative.builder().description(request.description()).correct(request.correct()).build();
    }

    public QuestionResponse toResponse (Question question) {

        List<AlternativeResponse> alternatives = question.getAlternatives().stream().map(this::toResponse).toList();

        return new QuestionResponse(question.getId(), question.getStatement(), alternatives);
    }

    public AlternativeResponse toResponse (Alternative alternative) {

        return new AlternativeResponse(alternative.getId(), alternative.getDescription(), alternative.isCorrect());
    }
}