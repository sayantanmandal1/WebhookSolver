package com.challenge.webhook.config;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ChallengeProperties validation.
 * Verifies that configuration validation works correctly and fails fast
 * with clear error messages for invalid configurations.
 * 
 * Requirements: 8.3, 8.4
 */
class ChallengePropertiesValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidConfiguration() {
        // Given: Valid configuration
        ChallengeProperties properties = new ChallengeProperties();
        properties.getUser().setName("John Doe");
        properties.getUser().setRegNo("REG12347");
        properties.getUser().setEmail("john@example.com");

        // When: Validating
        Set<ConstraintViolation<ChallengeProperties>> violations = validator.validate(properties);

        // Then: No violations
        assertTrue(violations.isEmpty(), "Valid configuration should have no violations");
    }

    @Test
    void testMissingUserName() {
        // Given: Configuration with missing name
        ChallengeProperties properties = new ChallengeProperties();
        properties.getUser().setName("");
        properties.getUser().setRegNo("REG12347");
        properties.getUser().setEmail("john@example.com");

        // When: Validating
        Set<ConstraintViolation<ChallengeProperties>> violations = validator.validate(properties);

        // Then: Should have violation with clear message
        assertFalse(violations.isEmpty(), "Missing name should cause violation");
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().contains("User name is required")),
            "Should have clear error message for missing name");
    }

    @Test
    void testMissingRegistrationNumber() {
        // Given: Configuration with missing regNo
        ChallengeProperties properties = new ChallengeProperties();
        properties.getUser().setName("John Doe");
        properties.getUser().setRegNo("");
        properties.getUser().setEmail("john@example.com");

        // When: Validating
        Set<ConstraintViolation<ChallengeProperties>> violations = validator.validate(properties);

        // Then: Should have violation with clear message
        assertFalse(violations.isEmpty(), "Missing regNo should cause violation");
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().contains("Registration number is required")),
            "Should have clear error message for missing regNo");
    }

    @Test
    void testInvalidEmail() {
        // Given: Configuration with invalid email
        ChallengeProperties properties = new ChallengeProperties();
        properties.getUser().setName("John Doe");
        properties.getUser().setRegNo("REG12347");
        properties.getUser().setEmail("invalid-email");

        // When: Validating
        Set<ConstraintViolation<ChallengeProperties>> violations = validator.validate(properties);

        // Then: Should have violation with clear message
        assertFalse(violations.isEmpty(), "Invalid email should cause violation");
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().contains("Email must be valid")),
            "Should have clear error message for invalid email");
    }

    @Test
    void testRegistrationNumberWithoutTwoDigits() {
        // Given: Configuration with regNo not ending in two digits
        ChallengeProperties properties = new ChallengeProperties();
        properties.getUser().setName("John Doe");
        properties.getUser().setRegNo("REGABC");
        properties.getUser().setEmail("john@example.com");

        // When: Validating
        Set<ConstraintViolation<ChallengeProperties>> violations = validator.validate(properties);

        // Then: Should have violation with clear message
        assertFalse(violations.isEmpty(), "RegNo without two digits should cause violation");
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().contains("two digits")),
            "Should have clear error message about two digits requirement");
    }

    @Test
    void testRegistrationNumberTooShort() {
        // Given: Configuration with regNo less than 2 characters
        ChallengeProperties properties = new ChallengeProperties();
        properties.getUser().setName("John Doe");
        properties.getUser().setRegNo("1");
        properties.getUser().setEmail("john@example.com");

        // When: Validating
        Set<ConstraintViolation<ChallengeProperties>> violations = validator.validate(properties);

        // Then: Should have violation with clear message
        assertFalse(violations.isEmpty(), "Short regNo should cause violation");
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().contains("at least 2 characters")),
            "Should have clear error message about minimum length");
    }

    @Test
    void testValidRegistrationNumberVariations() {
        // Test various valid registration number formats
        String[] validRegNos = {
            "REG00",      // Ends with 00 (even)
            "REG01",      // Ends with 01 (odd)
            "REG99",      // Ends with 99 (odd)
            "ABC12",      // Short but valid
            "STUDENT123", // Longer format
            "12345"       // All digits
        };

        for (String regNo : validRegNos) {
            ChallengeProperties properties = new ChallengeProperties();
            properties.getUser().setName("John Doe");
            properties.getUser().setRegNo(regNo);
            properties.getUser().setEmail("john@example.com");

            Set<ConstraintViolation<ChallengeProperties>> violations = validator.validate(properties);
            assertTrue(violations.isEmpty(), 
                "RegNo '" + regNo + "' should be valid but got violations: " + violations);
        }
    }

    @Test
    void testInvalidRegistrationNumberVariations() {
        // Test various invalid registration number formats
        String[] invalidRegNos = {
            "REG1A",      // Ends with letter
            "REGAB",      // Ends with two letters
            "REG1",       // Only one digit at end
            "ABC",        // No digits at end
            ""            // Empty string
        };

        for (String regNo : invalidRegNos) {
            ChallengeProperties properties = new ChallengeProperties();
            properties.getUser().setName("John Doe");
            properties.getUser().setRegNo(regNo);
            properties.getUser().setEmail("john@example.com");

            Set<ConstraintViolation<ChallengeProperties>> violations = validator.validate(properties);
            assertFalse(violations.isEmpty(), 
                "RegNo '" + regNo + "' should be invalid but passed validation");
        }
    }

    @Test
    void testApiUrlValidation() {
        // Given: Configuration with missing API URLs
        ChallengeProperties properties = new ChallengeProperties();
        properties.getUser().setName("John Doe");
        properties.getUser().setRegNo("REG12347");
        properties.getUser().setEmail("john@example.com");
        properties.getApi().setWebhookGeneratorUrl("");
        properties.getApi().setSubmissionBaseUrl("");

        // When: Validating
        Set<ConstraintViolation<ChallengeProperties>> violations = validator.validate(properties);

        // Then: Should have violations for both URLs
        assertFalse(violations.isEmpty(), "Missing API URLs should cause violations");
        assertEquals(2, violations.size(), "Should have exactly 2 violations for missing URLs");
    }
}
