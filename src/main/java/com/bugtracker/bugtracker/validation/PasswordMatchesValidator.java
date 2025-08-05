package com.bugtracker.bugtracker.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;

/**
 * Validator to check if password and confirmation password fields match.
 * Uses reflection to access the specified password fields in the DTO.
 */
public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {
    
    private String passwordField;
    private String confirmPasswordField;
    
    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
        this.passwordField = constraintAnnotation.passwordField();
        this.confirmPasswordField = constraintAnnotation.confirmPasswordField();
    }
    
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        
        try {
            Field passwordFieldObj = value.getClass().getDeclaredField(passwordField);
            Field confirmPasswordFieldObj = value.getClass().getDeclaredField(confirmPasswordField);
            
            passwordFieldObj.setAccessible(true);
            confirmPasswordFieldObj.setAccessible(true);
            
            String password = (String) passwordFieldObj.get(value);
            String confirmPassword = (String) confirmPasswordFieldObj.get(value);
            
            if (password == null && confirmPassword == null) {
                return true;
            }
            
            boolean matches = password != null && password.equals(confirmPassword);
            
            if (!matches) {
                // Add validation error to the confirm password field
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                       .addPropertyNode(confirmPasswordField)
                       .addConstraintViolation();
            }
            
            return matches;
            
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Error validating password match", e);
        }
    }
} 