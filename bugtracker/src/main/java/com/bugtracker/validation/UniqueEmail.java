package com.bugtracker.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

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