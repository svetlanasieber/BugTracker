package com.bugtracker.project.service;

import com.bugtracker.project.model.Project;
import com.bugtracker.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectService {
    Project createProject(Project project);
    
    
    Project createProject(String name, String description);
    
    Optional<Project> findById(UUID id);
    List<Project> findAll();
    List<Project> findProjectsByUserId(Long userId);
    Project updateProject(Project project);
    
    
    Project updateProject(UUID id, String name, String description);
    
    void deleteProject(UUID id);
    void addUserToProject(UUID projectId, Long userId);
    void removeUserFromProject(UUID projectId, Long userId);
    long countUserProjects(Long userId);
    
    Project changeProjectStatus(UUID projectId, boolean isActive);
    List<Project> searchProjects(String keyword);
    
    
    List<Project> getAllProjects();
    Project getProjectById(UUID id);
    List<Project> getProjectsByUserId(Long userId);
    void assignUserToProject(Long userId, UUID projectId);
    void removeAllUsersFromProject(UUID projectId);

    List<Project> getProjectsForUser(String username);
    
    
    boolean isUserAuthorizedForProject(String username, UUID projectId);
  
    Project createProjectWithUsers(String name, String description, List<Long> userIds);

    Project updateProjectWithUsers(UUID projectId, String name, String description, List<Long> userIds);

    String performProjectDatabaseFix();
 
    Project createProjectFromDTO(com.bugtracker.web.dto.ProjectAdd projectAdd);

    Project updateProjectFromDTO(com.bugtracker.web.dto.ProjectUpdate projectUpdate);

    
} 
