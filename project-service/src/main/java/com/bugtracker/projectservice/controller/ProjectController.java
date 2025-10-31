package com.bugtracker.projectservice.controller;

import com.bugtracker.projectservice.dto.ProjectCreateRequest;
import com.bugtracker.projectservice.dto.ProjectDto;
import com.bugtracker.projectservice.dto.ProjectUpdateRequest;
import com.bugtracker.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Slf4j
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<List<ProjectDto>> getAllProjects() {
        log.info("Fetching all projects");
        List<ProjectDto> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<ProjectDto>> getProjectsForUser(@PathVariable String username) {
        log.info("Fetching projects for user: {}", username);
        List<ProjectDto> projects = projectService.getProjectsForUser(username);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/user-id/{userId}")
    public ResponseEntity<List<ProjectDto>> getProjectsForUserId(@PathVariable Long userId) {
        log.info("Fetching projects for user ID: {}", userId);
        List<ProjectDto> projects = projectService.getProjectsForUserId(userId);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> getProjectById(@PathVariable UUID id) {
        log.info("Fetching project with ID: {}", id);
        ProjectDto project = projectService.getProjectById(id);
        return ResponseEntity.ok(project);
    }

    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@Valid @RequestBody ProjectCreateRequest request) {
        log.info("Creating new project: {}", request.getName());
        ProjectDto createdProject = projectService.createProject(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectDto> updateProject(@PathVariable UUID id, 
                                                  @Valid @RequestBody ProjectUpdateRequest request) {
        log.info("Updating project: {}", id);
        ProjectDto updatedProject = projectService.updateProject(id, request);
        return ResponseEntity.ok(updatedProject);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID id) {
        log.info("Deleting project: {}", id);
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{projectId}/members/{userId}")
    public ResponseEntity<ProjectDto> addUserToProject(@PathVariable UUID projectId, 
                                                      @PathVariable Long userId) {
        log.info("Adding user {} to project {}", userId, projectId);
        ProjectDto updatedProject = projectService.addUserToProject(projectId, userId);
        return ResponseEntity.ok(updatedProject);
    }

    @DeleteMapping("/{projectId}/members/{userId}")
    public ResponseEntity<ProjectDto> removeUserFromProject(@PathVariable UUID projectId, 
                                                           @PathVariable Long userId) {
        log.info("Removing user {} from project {}", userId, projectId);
        ProjectDto updatedProject = projectService.removeUserFromProject(projectId, userId);
        return ResponseEntity.ok(updatedProject);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProjectDto>> searchProjects(@RequestParam("q") String keyword) {
        log.info("Searching projects with keyword: {}", keyword);
        List<ProjectDto> projects = projectService.searchProjects(keyword);
        return ResponseEntity.ok(projects);
    }
}
