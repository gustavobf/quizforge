package com.quizforge.infrastructure.security;

import com.quizforge.application.port.out.CurrentUserPort;
import com.quizforge.domain.enumtype.Role;
import com.quizforge.domain.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityCurrentUserAdapter implements CurrentUserPort {
    @Override
    public Long requireCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser principal)) {
            throw new UnauthorizedException("Authentication required");
        }
        return principal.id();
    }

    @Override
    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser principal)) {
            return false;
        }
        return principal.role() == Role.ADMIN;
    }
}
