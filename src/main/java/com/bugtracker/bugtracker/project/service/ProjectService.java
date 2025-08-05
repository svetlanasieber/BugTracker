package com.bugtracker.bugtracker.project.service;

import com.bugtracker.bugtracker.project.model.Project;
import com.bugtracker.bugtracker.user.model.User;

import java.util.List;
import java.util.Optional;

public interface ProjectService {
    Project createProject(Project project);
    
    // New methods for creating a project with name and description
    Project createProject(String name, String description);
    
    Optional<Project> findById(Long id);
    List<Project> findAll();
    List<Project> findProjectsByUserId(Long userId);
    Project updateProject(Project project);
    
    // New methods for updating a project with specific fields
    Project updateProject(Long id, String name, String description);
    
    void deleteProject(Long id);
    void addUserToProject(Long projectId, Long userId);
    void removeUserFromProject(Long projectId, Long userId);
    long countUserProjects(Long userId);
    
    Project changeProjectStatus(Long projectId, boolean isActive);
    List<Project> searchProjects(String keyword);
    
    // Additional methods used in the controller
    List<Project> getAllProjects();
    Project getProjectById(Long id);
    List<Project> getProjectsByUserId(Long userId);
    void assignUserToProject(Long userId, Long projectId);
    void removeAllUsersFromProject(Long projectId);
    
    // ============= NEW BUSINESS LOGIC METHODS =============
    
    /**
     * Gets projects based on user role and authorization.
     * Admin users see all projects, regular users see only their assigned projects.
     */
    List<Project> getProjectsForUser(String username);
    
    /**
     * Checks if a user is authorized to view a specific project.
     * Admin users can view all projects, regular users can only view projects they're assigned to.
     */
    boolean isUserAuthorizedForProject(String username, Long projectId);
    
    /**
     * Creates a project with assigned users in a single transaction.
     */
    Project createProjectWithUsers(String name, String description, List<Long> userIds);
    
    /**
     * Updates a project with its assigned users in a single transaction.
     */
    Project updateProjectWithUsers(Long projectId, String name, String description, List<Long> userIds);
    
    /**
     * Performs database fix operations for projects setup.
     * This includes creating admin user, roles, and initial project if needed.
     */
    String performProjectDatabaseFix();
    
    // ============= DTO-BASED METHODS =============
    
    /**
     * Creates a project from ProjectAdd DTO.
     */
    Project createProjectFromDTO(com.bugtracker.bugtracker.web.dto.ProjectAdd projectAdd);
    
    /**
     * Updates a project from ProjectUpdate DTO.
     */
    Project updateProjectFromDTO(com.bugtracker.bugtracker.web.dto.ProjectUpdate projectUpdate);
} 