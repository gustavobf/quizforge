package com.quizforge.adapter.in.web.mapper;

import com.quizforge.adapter.in.web.dto.request.*;
import com.quizforge.domain.model.*;
import org.springframework.stereotype.*;

@Component
public class QuestionWebMapper {

    public Alternative toAlternativeDomain (CreateAlternativeRequest request) {
        return Alternative.builder().description(request.getDescription()).correct(request.getCorrect()).build();
    }

}