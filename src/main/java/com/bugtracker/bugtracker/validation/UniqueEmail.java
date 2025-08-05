package com.bugtracker.bugtracker.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validation annotation to ensure email addresses are unique in the system.
 * Used during user registration and profile updates.
 */
@Documented
@Constraint(validatedBy = UniqueEmailValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueEmail {
    String message() default "Email address is already registered";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    
    long excludeUserId() default -1;
} 