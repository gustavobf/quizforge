package com.quizforge.infrastructure.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.servers.*;
import io.swagger.v3.oas.models.tags.*;
import org.springframework.context.annotation.*;

import java.util.*;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI () {
        return new OpenAPI().info(
                        new Info().title("QuizForge API").description("API for exam simulation and question management system")
                                .version("1.0.0").contact(new Contact().name("QuizForge Team").email("support@quizforge.com")
                                        .url("https://quizforge.com"))
                                .license(new License().name("MIT License").url("https://opensource.org/licenses/MIT"))).servers(
                        List.of(new Server().url("http://localhost:8080").description("Development Server"),
                                new Server().url("https://api.quizforge.com").description("Production Server")))
                .tags(List.of(new Tag().name("Exams").description("Endpoints for exam management"),
                        new Tag().name("Questions").description("Endpoints for question management"),
                        new Tag().name("Template").description("Endpoints for template downloads")));
    }
}