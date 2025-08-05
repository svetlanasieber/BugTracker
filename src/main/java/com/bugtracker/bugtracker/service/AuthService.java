package com.bugtracker.bugtracker.service;

/**
 * Service interface for authentication and security-related operations.
 * Provides methods to access current user context and authentication information.
 */
public interface AuthService {

    /**
     * Gets the username of the currently authenticated user.
     * 
     * @return The username (email) of the current user
     * @throws RuntimeException if no authenticated user is found
     */
    String getCurrentUsername();

    /**
     * Gets the ID of the currently authenticated user.
     * 
     * @return The user ID of the current user
     * @throws RuntimeException if no authenticated user is found
     */
    Long getCurrentUserId();

    /**
     * Checks if the current user has admin role.
     * 
     * @return true if the current user is an admin, false otherwise
     */
    boolean isCurrentUserAdmin();
} 