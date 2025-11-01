package com.bugtracker.projectservice.exception;

import java.util.UUID;

public class ProjectNotFoundException extends RuntimeException {
    public ProjectNotFoundException(String message) {
        super(message);
    }
    public ProjectNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    public ProjectNotFoundException(UUID id) {
        super("Project not found with id: " + id);
    }
}
