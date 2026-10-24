package com.quizforge.application.port.in;

import com.quizforge.adapter.in.web.dto.response.*;
import org.springframework.data.domain.*;

import java.util.*;

public interface GetHistoryUseCase {
    HistorySummaryResponse getSummary ();

    Page<HistoryExamResponse> getExams (Pageable pageable);

    HistoryExamResponse getExamDetail (Long examId);

    List<SubjectStatsResponse> getSubjectStats ();
}