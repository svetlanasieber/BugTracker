package com.bugtracker.bugtracker.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

/**
 * Validator for file upload constraints including size and type validation.
 */
public class FileUploadValidator implements ConstraintValidator<ValidFileUpload, MultipartFile> {
    
    private long maxSize;
    private String[] allowedTypes;
    private boolean required;
    
    @Override
    public void initialize(ValidFileUpload constraintAnnotation) {
        this.maxSize = constraintAnnotation.maxSize();
        this.allowedTypes = constraintAnnotation.allowedTypes();
        this.required = constraintAnnotation.required();
    }
    
    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        // If file is null or empty
        if (file == null || file.isEmpty()) {
            return !required; // Valid if not required, invalid if required
        }
        
        // Check file size
        if (file.getSize() > maxSize) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                String.format("File size exceeds maximum allowed size of %d bytes", maxSize))
                .addConstraintViolation();
            return false;
        }
        
        // Check file type
        String contentType = file.getContentType();
        if (contentType == null || !Arrays.asList(allowedTypes).contains(contentType)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                String.format("File type not allowed. Allowed types: %s", Arrays.toString(allowedTypes)))
                .addConstraintViolation();
            return false;
        }
        
        return true;
    }
} 