package com.loan_calculator.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Explicitly exposes a shared {@link ObjectMapper} bean so integration tests and controllers can rely on
 * Spring's dependency injection rather than creating ad-hoc mapper instances.
 * <p>
 * Spring Boot normally auto-configures an {@code ObjectMapper} when Jackson is on the classpath. However,
 * some environments may skip that auto-configuration (for example, if dependency resolution fails). Defining
 * this bean keeps the application resilient and ensures JSON serialization/deserialization is always available.
 */
@Configuration
public class JacksonConfig {

    /**
     * Provides a singleton {@link ObjectMapper} managed by the Spring container.
     *
     * @return a default-configured mapper suitable for JSON serialization and deserialization throughout the app
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
