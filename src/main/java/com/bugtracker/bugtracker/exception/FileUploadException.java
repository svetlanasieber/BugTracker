package com.bugtracker.bugtracker.exception;

/**
 * Exception thrown when file upload operations fail.
 * Used for profile image uploads and other file operations.
 */
public class FileUploadException extends RuntimeException {
    
    public FileUploadException(String message) {
        super(message);
    }
    
    public FileUploadException(String message, Throwable cause) {
        super(message, cause);
    }
} 