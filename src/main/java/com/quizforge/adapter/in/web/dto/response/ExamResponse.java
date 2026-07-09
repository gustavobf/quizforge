package com.quizforge.adapter.in.web.dto.response;

import com.quizforge.domain.enumtype.*;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamResponse {

    private Long examId;
    private String title;
    private Integer totalQuestions;
    private String subjectName;
    private ExamStatus status;
}