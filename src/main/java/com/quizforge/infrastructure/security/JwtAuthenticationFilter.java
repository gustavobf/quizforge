package com.quizforge.infrastructure.security;

import com.quizforge.adapter.out.persistence.entity.UserJpaEntity;
import com.quizforge.adapter.out.persistence.repository.SpringTokenBlacklistRepository;
import com.quizforge.adapter.out.persistence.repository.SpringUserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final SpringUserRepository userRepository;
    private final SpringTokenBlacklistRepository tokenBlacklistRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            SpringUserRepository userRepository,
            SpringTokenBlacklistRepository tokenBlacklistRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.tokenBlacklistRepository = tokenBlacklistRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = extractToken(request);
        if (token == null || !jwtService.isValidAccessToken(token) || tokenBlacklistRepository.existsByToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        AuthenticatedUser authenticatedUser = jwtService.extractUser(token);
        UserJpaEntity user = userRepository.findById(authenticatedUser.id()).orElse(null);
        if (user == null || !user.isActive()) {
            filterChain.doFilter(request, response);
            return;
        }

        var authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());
        var auth = new UsernamePasswordAuthenticationToken(
                new AuthenticatedUser(user.getId(), user.getEmail(), user.getRole()),
                null,
                List.of(authority));

        SecurityContextHolder.getContext().setAuthentication(auth);
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith(PREFIX)) {
            return null;
        }
        return header.substring(PREFIX.length());
    }
}
