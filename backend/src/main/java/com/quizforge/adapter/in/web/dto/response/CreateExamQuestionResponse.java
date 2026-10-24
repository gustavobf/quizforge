package com.quizforge.adapter.in.web.dto.response;

import com.quizforge.domain.enumtype.*;
import lombok.*;

import java.util.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateExamQuestionResponse {
    private Long questionId;
    private String statement;
    private List<CreateExamAlternativeResponse> alternatives;
    private Integer orderNumber;
    private QuestionType questionType;
}