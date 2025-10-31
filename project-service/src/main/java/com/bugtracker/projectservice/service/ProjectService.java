package com.bugtracker.projectservice.service;

import com.bugtracker.projectservice.dto.ProjectCreateRequest;
import com.bugtracker.projectservice.dto.ProjectDto;
import com.bugtracker.projectservice.dto.ProjectUpdateRequest;
import com.bugtracker.projectservice.model.Project;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectService {
    
    ProjectDto createProject(ProjectCreateRequest request);
    
    ProjectDto updateProject(UUID id, ProjectUpdateRequest request);
    
    void deleteProject(UUID id);
    
    ProjectDto getProjectById(UUID id);
    
    List<ProjectDto> getAllProjects();
    
    List<ProjectDto> getProjectsForUser(String username);
    
    List<ProjectDto> getProjectsForUserId(Long userId);
    
    boolean isUserAuthorizedForProject(String username, UUID projectId);
    
    ProjectDto addUserToProject(UUID projectId, Long userId);
    
    ProjectDto removeUserFromProject(UUID projectId, Long userId);
    
    List<ProjectDto> searchProjects(String keyword);
}
