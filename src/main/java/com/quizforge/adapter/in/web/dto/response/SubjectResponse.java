package com.quizforge.adapter.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.*;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Subject response data")
public class SubjectResponse {

    @Schema(description = "Subject ID", example = "1")
    private Long id;

    @Schema(description = "Subject name", example = "Java")
    private String name;

    @Schema(description = "Subject description", example = "Java programming language")
    private String description;
}