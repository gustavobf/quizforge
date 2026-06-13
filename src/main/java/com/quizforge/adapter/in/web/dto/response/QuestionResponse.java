package com.quizforge.adapter.in.web.dto.response;

import java.util.List;

public record QuestionResponse(Long id, String statement, List<AlternativeResponse> alternatives) {
}