package com.bugtracker.exception;

public class ProjectNotFoundException extends RuntimeException {
    
    public ProjectNotFoundException(String message) {
        super(message);
    }
    
    public ProjectNotFoundException(Long id) {
        super("Project not found with ID: " + id);
    }
} 