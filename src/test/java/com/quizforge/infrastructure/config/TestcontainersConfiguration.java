package com.quizforge.infrastructure.config;

import org.springframework.boot.test.context.*;
import org.springframework.boot.testcontainers.service.connection.*;
import org.springframework.context.annotation.*;
import org.testcontainers.containers.*;
import org.testcontainers.utility.*;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test");
    }
}