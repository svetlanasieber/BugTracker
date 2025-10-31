package com.bugtracker.exception;

public class UserAlreadyExistsException extends RuntimeException {
    
    public UserAlreadyExistsException(String message) {
        super(message);
    }
    
    public UserAlreadyExistsException(String email, String field) {
        super("User with " + field + " '" + email + "' already exists");
    }
} 