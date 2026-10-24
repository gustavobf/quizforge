package com.quizforge.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DevelopmentProfileConfigTest {
    private Properties configuration() {
        return configuration(new ClassPathResource("application-dev.yml"));
    }

    private Properties configuration(Resource resource) {
        var factory = new YamlPropertiesFactoryBean();
        factory.setResources(resource);
        Properties properties = factory.getObject();
        assertNotNull(properties);
        return properties;
    }

    @Test
    void usesTheExistingPostgresDatabase() {
        assertEquals("jdbc:postgresql://localhost:5433/quizforge_dev",
                configuration().getProperty("spring.datasource.url"));
    }

    @Test
    void doesNotStartOrOverrideAnExistingDockerDatabase() {
        assertEquals("false", configuration().getProperty("spring.docker.compose.enabled"));
    }

    @Test
    void preservesExistingData() {
        Properties properties = configuration();
        assertEquals("update", properties.getProperty("spring.jpa.hibernate.ddl-auto"));
        assertEquals("never", properties.getProperty("spring.sql.init.mode"));
    }

    @Test
    void usesThePortExpectedByTheFrontend() {
        assertEquals("8081", configuration().getProperty("server.port"));
    }

    @Test
    void defaultsToSafeLocalDevelopmentWithoutManagingDocker() {
        Properties properties = configuration(new ClassPathResource("application.yml"));
        assertEquals("dev", properties.getProperty("spring.profiles.default"));
        assertEquals("false", properties.getProperty("spring.docker.compose.enabled"));
        assertEquals("update", properties.getProperty("spring.jpa.hibernate.ddl-auto"));
        assertEquals("never", properties.getProperty("spring.sql.init.mode"));
    }

    @Test
    void localComposeProvidesOnlyPostgresAndReusesTheDevelopmentVolume() {
        Properties properties = configuration(new FileSystemResource("docker-compose.yml"));
        assertEquals("quizforge-srv", properties.getProperty("name"));
        assertEquals("postgres:16-alpine", properties.getProperty("services.postgres.image"));
        assertEquals("5433:5432", properties.getProperty("services.postgres.ports[0]"));
        assertEquals("quizforge_dev", properties.getProperty("services.postgres.environment.POSTGRES_DB"));
        assertEquals("dev-postgres-data:/var/lib/postgresql/data", properties.getProperty("services.postgres.volumes[0]"));
        assertTrue(properties.stringPropertyNames().stream().filter(key -> key.startsWith("services."))
                .allMatch(key -> key.startsWith("services.postgres.")));
    }

    @Test
    void dockerStackKeepsAllServicesAndUsesItsOwnInternalDatabase() {
        Properties properties = configuration(new FileSystemResource("../docker-compose.yml"));
        assertEquals("quizforge-stack", properties.getProperty("name"));
        assertNotNull(properties.getProperty("services.frontend.build.context"));
        assertNotNull(properties.getProperty("services.backend.build.context"));
        assertNotNull(properties.getProperty("services.postgres.image"));
        assertEquals("docker", properties.getProperty("services.backend.environment.SPRING_PROFILES_ACTIVE"));
        assertTrue(properties.getProperty("services.backend.environment.SPRING_DATASOURCE_URL")
                .startsWith("jdbc:postgresql://postgres:5432/"));
        assertEquals("postgres-data:/var/lib/postgresql/data", properties.getProperty("services.postgres.volumes[0]"));
    }
}