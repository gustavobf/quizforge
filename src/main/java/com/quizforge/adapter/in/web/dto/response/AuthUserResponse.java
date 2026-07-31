package com.quizforge.adapter.in.web.dto.response;

import com.quizforge.domain.enumtype.Role;

public record AuthUserResponse(
        Long id,
        String name,
        String email,
        Role role
) {
}
