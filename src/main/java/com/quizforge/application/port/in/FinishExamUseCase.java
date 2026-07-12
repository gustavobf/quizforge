package com.quizforge.application.port.in;

import com.quizforge.adapter.in.web.dto.request.*;
import com.quizforge.adapter.in.web.dto.response.*;

public interface FinishExamUseCase {
    ExamResultResponse execute (Long examId, FinishExamRequest request);
}