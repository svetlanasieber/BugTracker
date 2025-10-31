package com.bugtracker.exception;

public class BugNotFoundException extends RuntimeException {
    
    public BugNotFoundException(String message) {
        super(message);
    }
    
    public BugNotFoundException(Long id) {
        super("Bug not found with ID: " + id);
    }
} 