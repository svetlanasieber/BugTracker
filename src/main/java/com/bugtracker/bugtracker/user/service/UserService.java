package com.bugtracker.bugtracker.user.service;

import com.bugtracker.bugtracker.web.dto.PasswordChange;
import com.bugtracker.bugtracker.web.dto.ProfileEdit;
import com.bugtracker.bugtracker.web.dto.UserRegister;
import com.bugtracker.bugtracker.user.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserService {
    User registerUser(UserRegister userRegister);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    List<User> findAllUsers();
    void changeUserRole(Long userId, String roleName);
    boolean existsByEmail(String email);
    
    // Additional method used in controller
    User findByUsername(String username);
    
    // Method used in ProjectController
    List<User> findAll();
    
    // Method for updating user information
    User updateUser(User user);
    
    // Added methods to move business logic from controller to service
    User updateUserProfile(String username, ProfileEdit profileEdit);
    boolean changeUserPassword(String username, PasswordChange passwordChange);
    User getCurrentUserWithStats(String username);
    
    // ============= ADMIN BUSINESS LOGIC METHODS =============
    
    /**
     * Resets or creates admin user with default credentials.
     * Ensures admin has proper roles assigned.
     */
    String resetAdminUser();
    
    /**
     * Gets comprehensive debug information about the system.
     * Includes user count, roles, admin users, etc.
     */
    Map<String, Object> getSystemDebugInfo();
    
    /**
     * Checks if a user has admin privileges.
     */
    boolean isUserAdmin(String username);
    
    /**
     * Creates a new user with specified roles (admin-only function).
     * @param firstName User's first name
     * @param lastName User's last name  
     * @param email User's email
     * @param password Raw password (will be encoded)
     * @param roleNames List of role names to assign
     * @return Created user
     */
    User createUserWithRoles(String firstName, String lastName, String email, String password, List<String> roleNames);
} 