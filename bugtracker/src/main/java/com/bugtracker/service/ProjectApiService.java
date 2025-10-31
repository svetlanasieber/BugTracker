package com.bugtracker.service;

import com.bugtracker.client.ProjectClient;
import com.bugtracker.client.dto.ProjectCreateRequest;
import com.bugtracker.client.dto.ProjectDto;
import com.bugtracker.client.dto.ProjectUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectApiService {

    private final ProjectClient projectClient;
    private final AuthService authService;

    public List<ProjectDto> getProjectsForUser(String username) {
        return projectClient.getProjectsForUser(username);
    }

    public ProjectDto createProject(ProjectCreateRequest request, String username) {
        Long currentUserId = authService.getCurrentUserId();
        request.setCreatedByUserId(currentUserId);
        request.setCreatedByUsername(username);
        
        return projectClient.createProject(request);
    }

    public ProjectDto updateProject(UUID id, ProjectUpdateRequest request) {
        return projectClient.updateProject(id, request);
    }

    public void deleteProject(UUID id) {
        projectClient.deleteProject(id);
    }
}

