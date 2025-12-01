package com.challenge.webhook.config.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator implementation for registration number format.
 * Validates that:
 * 1. The registration number is not null or blank
 * 2. The registration number ends with at least two digits
 * 3. The last two characters can be parsed as a valid integer
 */
public class RegistrationNumberValidator implements ConstraintValidator<ValidRegistrationNumber, String> {

    @Override
    public void initialize(ValidRegistrationNumber constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String regNo, ConstraintValidatorContext context) {
        // Null or blank values should be handled by @NotBlank
        if (regNo == null || regNo.isBlank()) {
            return false;
        }

        // Check if registration number has at least 2 characters
        if (regNo.length() < 2) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "Registration number must have at least 2 characters"
            ).addConstraintViolation();
            return false;
        }

        // Extract last two characters
        String lastTwoChars = regNo.substring(regNo.length() - 2);

        // Verify both characters are digits
        if (!lastTwoChars.matches("\\d{2}")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "Registration number must end with exactly two digits (found: '" + lastTwoChars + "')"
            ).addConstraintViolation();
            return false;
        }

        // Verify the last two digits can be parsed as an integer
        try {
            int lastTwoDigits = Integer.parseInt(lastTwoChars);
            // Valid range check (00-99)
            if (lastTwoDigits < 0 || lastTwoDigits > 99) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                    "Last two digits must be between 00 and 99 (found: " + lastTwoDigits + ")"
                ).addConstraintViolation();
                return false;
            }
        } catch (NumberFormatException e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "Last two characters must be valid digits (found: '" + lastTwoChars + "')"
            ).addConstraintViolation();
            return false;
        }

        return true;
    }
}
