package com.bugtracker.bugtracker.validation;

import com.bugtracker.bugtracker.user.repository.UserRepository;
import com.bugtracker.bugtracker.user.model.User;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {
    
    private final UserRepository userRepository;
    
    @Override
    public void initialize(UniqueEmail constraintAnnotation) {
        // Nothing to initialize
    }
    
    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.trim().isEmpty()) {
            return true; // Let @NotBlank handle empty validation
        }
        
        return userRepository.findByEmail(email.trim().toLowerCase()).isEmpty();
    }
} 
