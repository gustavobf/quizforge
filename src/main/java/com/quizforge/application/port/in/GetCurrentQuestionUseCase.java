package com.quizforge.application.port.in;

import com.quizforge.adapter.in.web.dto.response.*;

public interface GetCurrentQuestionUseCase {
    ExamQuestionResponse execute (Long examId);
}