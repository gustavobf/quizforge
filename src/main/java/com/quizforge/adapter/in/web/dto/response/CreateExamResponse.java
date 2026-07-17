package com.quizforge.adapter.in.web.dto.response;

import lombok.*;

import java.util.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateExamResponse {
    private Long examId;
    private String title;
    private Integer totalQuestions;
    private String subjectName;
    private List<CreateExamQuestionResponse> questions;
}