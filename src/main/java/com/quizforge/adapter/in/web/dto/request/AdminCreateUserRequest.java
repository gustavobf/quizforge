package com.quizforge.adapter.in.web.dto.request;

import com.quizforge.domain.enumtype.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AdminCreateUserRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Email String email,
        @NotBlank String password,
        @NotNull Role role
) {
}
