package com.bugtracker.project.service;

import com.bugtracker.project.model.Project;
import com.bugtracker.user.model.User;

import java.util.List;
import java.util.Optional;

public interface ProjectService {
    Project createProject(Project project);
    
    
    Project createProject(String name, String description);
    
    Optional<Project> findById(Long id);
    List<Project> findAll();
    List<Project> findProjectsByUserId(Long userId);
    Project updateProject(Project project);
    
    
    Project updateProject(Long id, String name, String description);
    
    void deleteProject(Long id);
    void addUserToProject(Long projectId, Long userId);
    void removeUserFromProject(Long projectId, Long userId);
    long countUserProjects(Long userId);
    
    Project changeProjectStatus(Long projectId, boolean isActive);
    List<Project> searchProjects(String keyword);
    
    
    List<Project> getAllProjects();
    Project getProjectById(Long id);
    List<Project> getProjectsByUserId(Long userId);
    void assignUserToProject(Long userId, Long projectId);
    void removeAllUsersFromProject(Long projectId);
    
    
    
    
    List<Project> getProjectsForUser(String username);
    
    
    boolean isUserAuthorizedForProject(String username, Long projectId);
    
    
    Project createProjectWithUsers(String name, String description, List<Long> userIds);
    
    
    Project updateProjectWithUsers(Long projectId, String name, String description, List<Long> userIds);
    
    
    String performProjectDatabaseFix();
    
    
    
    
    Project createProjectFromDTO(com.bugtracker.web.dto.ProjectAdd projectAdd);
    
    
    Project updateProjectFromDTO(com.bugtracker.web.dto.ProjectUpdate projectUpdate);
} 