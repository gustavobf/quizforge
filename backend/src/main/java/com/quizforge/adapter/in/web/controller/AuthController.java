package com.quizforge.adapter.in.web.controller;

import com.quizforge.adapter.in.web.dto.request.LoginRequest;
import com.quizforge.adapter.in.web.dto.request.RefreshTokenRequest;
import com.quizforge.adapter.in.web.dto.request.RegisterRequest;
import com.quizforge.adapter.in.web.dto.response.AuthResponse;
import com.quizforge.adapter.in.web.dto.response.TokenResponse;
import com.quizforge.application.service.AuthApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String PREFIX = "Bearer ";

    private final AuthApplicationService authApplicationService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authApplicationService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authApplicationService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authApplicationService.refresh(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        authApplicationService.logout(extractToken(authorizationHeader));
        return ResponseEntity.ok().build();
    }

    private String extractToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(PREFIX)) {
            return null;
        }
        return authorizationHeader.substring(PREFIX.length());
    }
}
