package com.bugtracker.projectservice.service;

import com.bugtracker.projectservice.dto.ProjectCreateRequest;
import com.bugtracker.projectservice.dto.ProjectDto;
import com.bugtracker.projectservice.dto.ProjectUpdateRequest;
import com.bugtracker.projectservice.model.Project;

import java.util.List;
import java.util.Optional;

public interface ProjectService {
    
    /**
     * Create a new project
     */
    ProjectDto createProject(ProjectCreateRequest request);
    
    /**
     * Update an existing project
     */
    ProjectDto updateProject(Long id, ProjectUpdateRequest request);
    
    /**
     * Delete a project by ID
     */
    void deleteProject(Long id);
    
    /**
     * Get project by ID
     */
    Optional<ProjectDto> getProjectById(Long id);
    
    /**
     * Get all projects
     */
    List<ProjectDto> getAllProjects();
    
    /**
     * Get projects for a specific user (created by or member of)
     */
    List<ProjectDto> getProjectsForUser(String username);
    
    /**
     * Get projects for a specific user ID
     */
    List<ProjectDto> getProjectsForUserId(Long userId);
    
    /**
     * Check if user is authorized to access project
     */
    boolean isUserAuthorizedForProject(String username, Long projectId);
    
    /**
     * Add user to project members
     */
    ProjectDto addUserToProject(Long projectId, Long userId);
    
    /**
     * Remove user from project members
     */
    ProjectDto removeUserFromProject(Long projectId, Long userId);
    
    /**
     * Search projects by name
     */
    List<ProjectDto> searchProjects(String keyword);
}

