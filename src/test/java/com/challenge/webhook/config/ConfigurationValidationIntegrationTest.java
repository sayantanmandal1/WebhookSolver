package com.challenge.webhook.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.bind.validation.BindValidationException;
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test to verify that the application fails fast on startup
 * with invalid configuration.
 * 
 * Requirements: 8.3, 8.4
 */
class ConfigurationValidationIntegrationTest {

    @Test
    void testApplicationFailsFastWithInvalidRegistrationNumber() {
        // Given: Application with invalid registration number
        SpringApplication app = new SpringApplication(TestApplication.class);
        app.setAdditionalProfiles("test-invalid-regNo");

        // When/Then: Application should fail to start
        Exception exception = assertThrows(Exception.class, () -> {
            ConfigurableApplicationContext context = app.run(
                "--challenge.user.name=John Doe",
                "--challenge.user.reg-no=INVALID",  // No digits at end
                "--challenge.user.email=john@example.com"
            );
            context.close();
        });

        // Verify the exception contains validation error message
        String message = getRootCauseMessage(exception);
        assertTrue(message.contains("two digits") || message.contains("Registration number"),
            "Error message should mention registration number validation: " + message);
    }

    @Test
    void testApplicationFailsFastWithMissingEmail() {
        // Given: Application with missing email
        SpringApplication app = new SpringApplication(TestApplication.class);
        app.setAdditionalProfiles("test-missing-email");

        // When/Then: Application should fail to start
        Exception exception = assertThrows(Exception.class, () -> {
            ConfigurableApplicationContext context = app.run(
                "--challenge.user.name=John Doe",
                "--challenge.user.reg-no=REG12347",
                "--challenge.user.email="  // Empty email
            );
            context.close();
        });

        // Verify the exception contains validation error message
        String message = getRootCauseMessage(exception);
        assertTrue(message.contains("Email") || message.contains("email"),
            "Error message should mention email validation: " + message);
    }

    @Test
    void testApplicationStartsWithValidConfiguration() {
        // Given: Application with valid configuration
        SpringApplication app = new SpringApplication(TestApplication.class);
        app.setAdditionalProfiles("test-valid");

        // When: Starting application
        ConfigurableApplicationContext context = null;
        try {
            context = app.run(
                "--challenge.user.name=John Doe",
                "--challenge.user.reg-no=REG12347",
                "--challenge.user.email=john@example.com",
                "--spring.main.web-application-type=none"  // Don't start web server
            );

            // Then: Application should start successfully
            assertNotNull(context);
            assertTrue(context.isActive());
            
            // Verify properties are loaded correctly
            ChallengeProperties properties = context.getBean(ChallengeProperties.class);
            assertNotNull(properties);
            assertEquals("John Doe", properties.getUser().getName());
            assertEquals("REG12347", properties.getUser().getRegNo());
            assertEquals("john@example.com", properties.getUser().getEmail());
        } finally {
            if (context != null) {
                context.close();
            }
        }
    }

    /**
     * Helper method to extract root cause message from exception chain
     */
    private String getRootCauseMessage(Throwable throwable) {
        Throwable cause = throwable;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        return cause.getMessage();
    }

    /**
     * Minimal test application configuration
     */
    @org.springframework.boot.autoconfigure.SpringBootApplication
    static class TestApplication {
        // Minimal configuration for testing
    }
}
