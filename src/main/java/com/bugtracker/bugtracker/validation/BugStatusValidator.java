package com.bugtracker.bugtracker.validation;

import com.bugtracker.bugtracker.bug.model.Bug;
import com.bugtracker.bugtracker.model.enums.BugStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BugStatusValidator implements ConstraintValidator<ValidBugStatus, String> {
    
    @Override
    public void initialize(ValidBugStatus constraintAnnotation) {
        // Nothing to initialize
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