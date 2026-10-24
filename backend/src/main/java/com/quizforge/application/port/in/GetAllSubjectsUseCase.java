package com.quizforge.application.port.in;

import com.quizforge.adapter.in.web.dto.response.*;

import java.util.*;

public interface GetAllSubjectsUseCase {
    List<SubjectResponse> execute ();
}