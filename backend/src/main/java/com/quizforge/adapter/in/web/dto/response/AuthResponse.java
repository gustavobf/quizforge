package com.quizforge.adapter.in.web.dto.response;

public record AuthResponse(
        AuthUserResponse user,
        TokenResponse token
) {
}
