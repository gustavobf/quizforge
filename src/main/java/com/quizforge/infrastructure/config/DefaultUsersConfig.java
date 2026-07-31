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
            createIfAbsent(userRepository, passwordEncoder, "Administrator", "admin@quizforge.local", "Admin@123", Role.ADMIN);
            createIfAbsent(userRepository, passwordEncoder, "Default User", "user@quizforge.local", "User@123", Role.USER);
        };
    }

    private void createIfAbsent(
            SpringUserRepository userRepository,
            PasswordEncoder passwordEncoder,
            String name,
            String email,
            String rawPassword,
            Role role) {
        if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        userRepository.save(UserJpaEntity.builder()
                .name(name)
                .email(email)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .role(role)
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build());
    }
}
