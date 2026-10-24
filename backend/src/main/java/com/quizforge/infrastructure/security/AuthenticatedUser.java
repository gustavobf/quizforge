package com.quizforge.infrastructure.security;

import com.quizforge.domain.enumtype.Role;

public record AuthenticatedUser(
        Long id,
        String email,
        Role role
) {
}
