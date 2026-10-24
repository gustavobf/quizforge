package com.quizforge.application.port.in;

import com.quizforge.adapter.in.web.dto.request.*;
import com.quizforge.adapter.in.web.dto.response.*;

public interface CreateSubjectUseCase {
    SubjectResponse execute (CreateSubjectRequest request);
}