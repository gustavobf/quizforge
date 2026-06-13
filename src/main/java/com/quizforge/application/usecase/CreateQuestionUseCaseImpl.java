package com.quizforge.application.usecase;

import com.quizforge.adapter.in.web.dto.request.*;
import com.quizforge.adapter.in.web.dto.response.*;
import com.quizforge.adapter.in.web.mapper.*;
import com.quizforge.application.port.out.*;
import com.quizforge.domain.entity.*;
import lombok.*;
import org.springframework.stereotype.*;

@Service
@RequiredArgsConstructor
public class CreateQuestionUseCaseImpl implements CreateQuestionUseCase {

    private final QuestionRepositoryPort repository;
    private final QuestionMapper mapper;

    @Override
    public QuestionResponse execute (CreateQuestionRequest request) {

        Question question = mapper.toDomain(request);

        question.validate();

        Question savedQuestion = repository.save(question);

        return mapper.toResponse(savedQuestion);
    }
}