package com.quizforge.infrastructure.security;

import com.quizforge.adapter.out.persistence.entity.UserJpaEntity;
import com.quizforge.domain.enumtype.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtService {

    private static final String ROLE_CLAIM = "role";
    private static final String USER_ID_CLAIM = "userId";
    private static final String TYPE_CLAIM = "type";
    private static final String ACCESS_TYPE = "ACCESS";
    private static final String REFRESH_TYPE = "REFRESH";

    private final JwtProperties jwtProperties;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String generateAccessToken(UserJpaEntity user) {
        return buildToken(user, ACCESS_TYPE, jwtProperties.getAccessTokenExpiration());
    }

    public String generateRefreshToken(UserJpaEntity user) {
        return buildToken(user, REFRESH_TYPE, jwtProperties.getRefreshTokenExpiration());
    }

    public long getAccessTokenExpirationSeconds() {
        return jwtProperties.getAccessTokenExpiration() / 1000;
    }

    public long getRefreshTokenExpirationSeconds() {
        return jwtProperties.getRefreshTokenExpiration() / 1000;
    }

    public boolean isValidAccessToken(String token) {
        return isValidTokenType(token, ACCESS_TYPE);
    }

    public boolean isValidRefreshToken(String token) {
        return isValidTokenType(token, REFRESH_TYPE);
    }

    public AuthenticatedUser extractUser(String token) {
        Claims claims = extractClaims(token);
        Long userId = claims.get(USER_ID_CLAIM, Long.class);
        String email = claims.getSubject();
        Role role = Role.valueOf(claims.get(ROLE_CLAIM, String.class));
        return new AuthenticatedUser(userId, email, role);
    }

    public LocalDateTime extractExpiration(String token) {
        Date expiration = extractClaims(token).getExpiration();
        return LocalDateTime.ofInstant(expiration.toInstant(), ZoneId.systemDefault());
    }

    private String buildToken(UserJpaEntity user, String type, long expirationMs) {
        Instant now = Instant.now();
        Instant exp = now.plusMillis(expirationMs);

        return Jwts.builder()
                .subject(user.getEmail())
                .claim(USER_ID_CLAIM, user.getId())
                .claim(ROLE_CLAIM, user.getRole().name())
                .claim(TYPE_CLAIM, type)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(getSecretKey(), Jwts.SIG.HS256)
                .compact();
    }

    private boolean isValidTokenType(String token, String expectedType) {
        try {
            Claims claims = extractClaims(token);
            String type = claims.get(TYPE_CLAIM, String.class);
            return expectedType.equals(type);
        } catch (RuntimeException ex) {
            return false;
        }
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }
}
