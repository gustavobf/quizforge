package com.quizforge.adapter.in.web.dto.response;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        long expiresIn,
        String tokenType
) {
}
