package com.challenge.webhook.config.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Custom validation annotation for registration number format.
 * Ensures the registration number ends with at least two digits that can be extracted
 * for question assignment logic.
 */
@Documented
@Constraint(validatedBy = {RegistrationNumberValidator.class})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRegistrationNumber {
    
    String message() default "Registration number must end with at least two digits";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
