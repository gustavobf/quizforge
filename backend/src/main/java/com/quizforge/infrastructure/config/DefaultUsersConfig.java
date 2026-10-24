package com.quizforge.infrastructure.config;

import com.quizforge.adapter.out.persistence.entity.UserJpaEntity;
import com.quizforge.adapter.out.persistence.repository.SpringUserRepository;
import com.quizforge.domain.enumtype.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
public class DefaultUsersConfig {

    @Bean
    CommandLineRunner seedDefaultUsers(SpringUserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            createOrUpdateDefaultUser(userRepository, passwordEncoder, "Administrator", "admin@quizforge.local", "Admin@123", Role.ADMIN);
            createOrUpdateDefaultUser(userRepository, passwordEncoder, "Default User", "user@quizforge.local", "User@123", Role.USER);
        };
    }

    private void createOrUpdateDefaultUser(
            SpringUserRepository userRepository,
            PasswordEncoder passwordEncoder,
            String name,
            String email,
            String rawPassword,
            Role role) {
        LocalDateTime now = LocalDateTime.now();
        UserJpaEntity user = userRepository.findByEmailIgnoreCase(email)
                .orElseGet(() -> UserJpaEntity.builder()
                        .email(email)
                        .createdAt(now)
                        .build());

        user.setName(name);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        user.setActive(true);
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(now);
        }
        user.setUpdatedAt(now);

        userRepository.save(user);
    }
}
