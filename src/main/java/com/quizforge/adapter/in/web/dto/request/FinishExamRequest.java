package com.quizforge.adapter.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.*;
import lombok.*;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FinishExamRequest {

    @Schema(description = "Map of question IDs to list of selected alternative IDs", example = "{\"101\": [1], \"102\": [2, 4], \"103\": []}")
    private Map<Long, List<Long>> answers;
}