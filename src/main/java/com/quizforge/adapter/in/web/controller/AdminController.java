package com.quizforge.adapter.in.web.controller;

import com.quizforge.adapter.in.web.dto.request.AdminCreateUserRequest;
import com.quizforge.adapter.in.web.dto.response.AuthUserResponse;
import com.quizforge.application.service.AuthApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminController {

    private final AuthApplicationService authApplicationService;

    @GetMapping
    public ResponseEntity<List<AuthUserResponse>> listUsers() {
        return ResponseEntity.ok(authApplicationService.listUsers());
    }

    @PostMapping
    public ResponseEntity<AuthUserResponse> createUser(@Valid @RequestBody AdminCreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authApplicationService.createUserByAdmin(request));
    }
}
