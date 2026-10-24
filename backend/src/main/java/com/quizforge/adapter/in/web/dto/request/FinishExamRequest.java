package com.quizforge.adapter.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.*;
import lombok.*;
import jakarta.validation.constraints.*;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FinishExamRequest {

    @Schema(description = "Map of question IDs to list of selected alternative IDs", example = "{\"101\": [1], \"102\": [2, 4], \"103\": []}")
    @NotNull
    private Map<@NotNull Long, @NotNull List<@NotNull Long>> answers;
}