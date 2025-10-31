package com.bugtracker.validation;

import com.bugtracker.bug.model.Bug;
import com.bugtracker.model.enums.BugStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BugStatusValidator implements ConstraintValidator<ValidBugStatus, String> {
    
    @Override
    public void initialize(ValidBugStatus constraintAnnotation) {
        
    }
    
    @Override
    public boolean isValid(String status, ConstraintValidatorContext context) {
        if (status == null || status.isEmpty()) {
            return false;
        }
        
        try {
            BugStatus.valueOf(status);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
} 