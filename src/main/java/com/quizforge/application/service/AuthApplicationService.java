package com.quizforge.application.service;

import com.quizforge.adapter.in.web.dto.request.AdminCreateUserRequest;
import com.quizforge.adapter.in.web.dto.request.ChangePasswordRequest;
import com.quizforge.adapter.in.web.dto.request.LoginRequest;
import com.quizforge.adapter.in.web.dto.request.RefreshTokenRequest;
import com.quizforge.adapter.in.web.dto.request.RegisterRequest;
import com.quizforge.adapter.in.web.dto.request.UpdateProfileRequest;
import com.quizforge.adapter.in.web.dto.response.AuthResponse;
import com.quizforge.adapter.in.web.dto.response.AuthUserResponse;
import com.quizforge.adapter.in.web.dto.response.TokenResponse;
import com.quizforge.adapter.out.persistence.entity.RefreshTokenJpaEntity;
import com.quizforge.adapter.out.persistence.entity.TokenBlacklistJpaEntity;
import com.quizforge.adapter.out.persistence.entity.UserJpaEntity;
import com.quizforge.adapter.out.persistence.repository.SpringRefreshTokenRepository;
import com.quizforge.adapter.out.persistence.repository.SpringTokenBlacklistRepository;
import com.quizforge.adapter.out.persistence.repository.SpringUserRepository;
import com.quizforge.application.port.out.CurrentUserPort;
import com.quizforge.domain.enumtype.Role;
import com.quizforge.domain.exception.InvalidCredentialsException;
import com.quizforge.domain.exception.BusinessException;
import com.quizforge.domain.exception.UnauthorizedException;
import com.quizforge.domain.exception.UserAlreadyExistsException;
import com.quizforge.domain.exception.UserNotFoundException;
import com.quizforge.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AuthApplicationService {

    private static final Pattern STRONG_PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!?.*-]).{8,}$");

    private final SpringUserRepository userRepository;
    private final SpringRefreshTokenRepository refreshTokenRepository;
    private final SpringTokenBlacklistRepository tokenBlacklistRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CurrentUserPort currentUserPort;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();
        validateNewUser(normalizedEmail, request.password());

        UserJpaEntity user = UserJpaEntity.builder()
                .name(request.name())
                .email(normalizedEmail)
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        UserJpaEntity saved = userRepository.save(user);
        return buildAuthResponse(saved);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        UserJpaEntity user = userRepository.findByEmailIgnoreCase(request.email().trim())
                .filter(UserJpaEntity::isActive)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        return buildAuthResponse(user);
    }

    @Transactional
    public TokenResponse refresh(RefreshTokenRequest request) {
        RefreshTokenJpaEntity refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())
                || !jwtService.isValidRefreshToken(refreshToken.getToken())
                || !refreshToken.getUser().isActive()) {
            throw new UnauthorizedException("Refresh token expired or invalid");
        }

        UserJpaEntity user = refreshToken.getUser();
        String accessToken = jwtService.generateAccessToken(user);
        return new TokenResponse(accessToken, refreshToken.getToken(), jwtService.getAccessTokenExpirationSeconds(), "Bearer");
    }

    @Transactional
    public void logout(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            throw new UnauthorizedException("Token not provided");
        }

        if (!jwtService.isValidAccessToken(accessToken)) {
            throw new UnauthorizedException("Invalid token");
        }

        if (!tokenBlacklistRepository.existsByToken(accessToken)) {
            tokenBlacklistRepository.save(TokenBlacklistJpaEntity.builder()
                    .token(accessToken)
                    .expiresAt(jwtService.extractExpiration(accessToken))
                    .createdAt(LocalDateTime.now())
                    .build());
        }

        refreshTokenRepository.deleteByUser_Id(currentUserPort.requireCurrentUserId());
    }

    @Transactional(readOnly = true)
    public AuthUserResponse getProfile() {
        UserJpaEntity user = getActiveCurrentUser();
        return toUserResponse(user);
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request, String currentAccessToken) {
        validatePasswordStrength(request.newPassword());

        UserJpaEntity user = getActiveCurrentUser();
        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        refreshTokenRepository.deleteByUser_Id(user.getId());
        blacklistTokenIfValid(currentAccessToken);
    }

    @Transactional
    public void deleteOwnAccount(String currentAccessToken) {
        UserJpaEntity user = getActiveCurrentUser();
        user.setActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        refreshTokenRepository.deleteByUser_Id(user.getId());
        blacklistTokenIfValid(currentAccessToken);
    }

    @Transactional
    public AuthUserResponse updateProfile(UpdateProfileRequest request) {
        UserJpaEntity user = getActiveCurrentUser();
        String updatedName = request.name() != null ? request.name().trim() : user.getName();
        String updatedEmail = request.email() != null ? request.email().trim().toLowerCase() : user.getEmail();

        if (updatedName.isBlank()) {
            throw new BusinessException("Name cannot be blank");
        }

        if (!Objects.equals(updatedEmail, user.getEmail())) {
            userRepository.findByEmailIgnoreCase(updatedEmail)
                    .filter(existing -> !existing.getId().equals(user.getId()))
                    .ifPresent(existing -> {
                        throw new UserAlreadyExistsException(updatedEmail);
                    });
        }

        user.setName(updatedName);
        user.setEmail(updatedEmail);
        user.setUpdatedAt(LocalDateTime.now());
        return toUserResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public List<AuthUserResponse> listUsers() {
        return userRepository.findAll().stream().map(this::toUserResponse).toList();
    }

    @Transactional
    public AuthUserResponse createUserByAdmin(AdminCreateUserRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();
        validateNewUser(normalizedEmail, request.password());

        UserJpaEntity user = UserJpaEntity.builder()
                .name(request.name())
                .email(normalizedEmail)
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(request.role())
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return toUserResponse(userRepository.save(user));
    }

    private void blacklistTokenIfValid(String token) {
        if (token == null || token.isBlank() || !jwtService.isValidAccessToken(token)) {
            return;
        }
        if (tokenBlacklistRepository.existsByToken(token)) {
            return;
        }
        tokenBlacklistRepository.save(TokenBlacklistJpaEntity.builder()
                .token(token)
                .expiresAt(jwtService.extractExpiration(token))
                .createdAt(LocalDateTime.now())
                .build());
    }

    private UserJpaEntity getActiveCurrentUser() {
        Long userId = currentUserPort.requireCurrentUserId();
        return userRepository.findById(userId)
                .filter(UserJpaEntity::isActive)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private void validateNewUser(String email, String password) {
        if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new UserAlreadyExistsException(email);
        }
        validatePasswordStrength(password);
    }

    private void validatePasswordStrength(String password) {
        if (!STRONG_PASSWORD_PATTERN.matcher(password).matches()) {
            throw new BusinessException(
                    "Password must have at least 8 chars with uppercase, lowercase, number and special char");
        }
    }

    private AuthResponse buildAuthResponse(UserJpaEntity user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        refreshTokenRepository.deleteByUser_Id(user.getId());
        refreshTokenRepository.save(RefreshTokenJpaEntity.builder()
                .user(user)
                .token(refreshToken)
                .expiresAt(LocalDateTime.now().plusSeconds(jwtService.getRefreshTokenExpirationSeconds()))
                .createdAt(LocalDateTime.now())
                .build());

        return new AuthResponse(
                toUserResponse(user),
                new TokenResponse(accessToken, refreshToken, jwtService.getAccessTokenExpirationSeconds(), "Bearer"));
    }

    private AuthUserResponse toUserResponse(UserJpaEntity user) {
        return new AuthUserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
