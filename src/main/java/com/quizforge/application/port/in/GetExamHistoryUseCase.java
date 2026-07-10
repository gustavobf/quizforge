package com.quizforge.application.port.in;

import com.quizforge.domain.model.*;

import java.util.*;

public interface GetExamHistoryUseCase {
    List<ExamHistory> execute (int page, int size);

    ExamHistorySummary getSummary ();

    List<ExamHistory> getBySubject (Long subjectId);

    ExamHistory getById (Long examId);
}