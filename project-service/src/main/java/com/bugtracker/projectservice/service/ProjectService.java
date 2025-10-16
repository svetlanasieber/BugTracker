package com.bugtracker.projectservice.service;

import com.bugtracker.projectservice.dto.ProjectCreateRequest;
import com.bugtracker.projectservice.dto.ProjectDto;
import com.bugtracker.projectservice.dto.ProjectUpdateRequest;
import com.bugtracker.projectservice.model.Project;

import java.util.List;
import java.util.Optional;

public interface ProjectService {
    

    ProjectDto createProject(ProjectCreateRequest request);
    

    ProjectDto updateProject(Long id, ProjectUpdateRequest request);
    

    void deleteProject(Long id);

    Optional<ProjectDto> getProjectById(Long id);
 
    List<ProjectDto> getAllProjects();

    List<ProjectDto> getProjectsForUser(String username);

    List<ProjectDto> getProjectsForUserId(Long userId);
  
    boolean isUserAuthorizedForProject(String username, Long projectId);

    ProjectDto addUserToProject(Long projectId, Long userId);

    ProjectDto removeUserFromProject(Long projectId, Long userId);
  
    List<ProjectDto> searchProjects(String keyword);
}

