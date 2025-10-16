package com.bugtracker.projectservice.exception;

/**
 * Exception thrown when a project is not found
 */
public class ProjectNotFoundException extends RuntimeException {
    
    public ProjectNotFoundException(String message) {
        super(message);
    }
    
    public ProjectNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ProjectNotFoundException(Long id) {
        super("Project not found with id: " + id);
    }
}

