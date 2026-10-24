package com.quizforge.adapter.in.web.controller;

import com.quizforge.adapter.in.web.dto.request.ChangePasswordRequest;
import com.quizforge.adapter.in.web.dto.request.UpdateProfileRequest;
import com.quizforge.adapter.in.web.dto.response.AuthUserResponse;
import com.quizforge.application.service.AuthApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private static final String PREFIX = "Bearer ";

    private final AuthApplicationService authApplicationService;

    @GetMapping("/profile")
    public ResponseEntity<AuthUserResponse> getProfile() {
        return ResponseEntity.ok(authApplicationService.getProfile());
    }

    @PutMapping("/profile")
    public ResponseEntity<AuthUserResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(authApplicationService.updateProfile(request));
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        authApplicationService.changePassword(request, extractToken(authorizationHeader));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/profile")
    public ResponseEntity<Void> deleteOwnAccount(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        authApplicationService.deleteOwnAccount(extractToken(authorizationHeader));
        return ResponseEntity.noContent().build();
    }

    private String extractToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(PREFIX)) {
            return null;
        }
        return authorizationHeader.substring(PREFIX.length());
    }
}
