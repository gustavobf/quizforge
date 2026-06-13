package com.quizforge.application.usecase;

import com.quizforge.adapter.in.web.dto.request.*;
import com.quizforge.adapter.in.web.dto.response.*;

public interface CreateQuestionUseCase {
    QuestionResponse execute (CreateQuestionRequest request);
}