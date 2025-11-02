package com.bugtracker.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FileUploadValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFileUpload {
    String message() default "Invalid file upload";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    long maxSize() default 10485760; 
    String[] allowedTypes() default {"image/jpeg", "image/png", "image/gif"};
    boolean required() default false;
} 