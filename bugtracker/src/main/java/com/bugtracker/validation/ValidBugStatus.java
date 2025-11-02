package com.bugtracker.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = BugStatusValidator.class)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBugStatus {
    String message() default "Invalid bug status";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
} 