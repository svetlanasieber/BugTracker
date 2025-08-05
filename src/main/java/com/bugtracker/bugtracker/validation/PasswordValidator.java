package com.bugtracker.bugtracker.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Validator for strong password requirements.
 * Enforces complexity rules for better security.
 */
public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {
    
    // At least 8 characters, with at least 1 uppercase, 1 lowercase, 1 digit, 1 special character
    private static final String PASSWORD_PATTERN = 
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    
    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
    
    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        // Nothing to initialize
    }
    
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }
        
        return pattern.matcher(password).matches();
    }
} 