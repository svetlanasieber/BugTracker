package com.bugtracker.bugtracker.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Custom validation for strong passwords.
 * Validates that password contains uppercase, lowercase, numbers, and special characters.
 */
@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    String message() default "Password must contain at least 8 characters, including uppercase, lowercase, number and special character";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
} 