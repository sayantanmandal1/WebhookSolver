package com.challenge.webhook.config;

import com.challenge.webhook.config.validation.ValidRegistrationNumber;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for the webhook SQL challenge application.
 * Binds values from application.yml/properties with validation.
 * 
 * The @Validated annotation ensures that validation occurs on startup,
 * causing the application to fail fast with clear error messages if
 * configuration is invalid.
 */
@Configuration
@ConfigurationProperties(prefix = "challenge")
@Validated
@Data
public class ChallengeProperties {

    @Valid
    private User user = new User();
    
    @Valid
    private Api api = new Api();

    @Data
    public static class User {
        @NotBlank(message = "User name is required")
        private String name;

        @NotBlank(message = "Registration number is required")
        @ValidRegistrationNumber
        private String regNo;

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        private String email;
    }

    @Data
    public static class Api {
        @NotBlank(message = "Webhook generator URL is required")
        private String webhookGeneratorUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        @NotBlank(message = "Submission base URL is required")
        private String submissionBaseUrl = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";
    }
}
