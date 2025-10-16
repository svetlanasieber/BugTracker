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
import java.util.Optional;

/**
 * REST Controller for Project Management Microservice
 * Provides CRUD operations for projects
 */
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Slf4j
public class ProjectController {

    private final ProjectService projectService;

    /**
     * Get all projects
     * GET /api/projects
     */
    @GetMapping
    public ResponseEntity<List<ProjectDto>> getAllProjects() {
        log.info("Fetching all projects");
        List<ProjectDto> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    /**
     * Get projects for specific user
     * GET /api/projects/user/{username}
     */
    @GetMapping("/user/{username}")
    public ResponseEntity<List<ProjectDto>> getProjectsForUser(@PathVariable String username) {
        log.info("Fetching projects for user: {}", username);
        List<ProjectDto> projects = projectService.getProjectsForUser(username);
        return ResponseEntity.ok(projects);
    }

    /**
     * Get projects for specific user ID
     * GET /api/projects/user-id/{userId}
     */
    @GetMapping("/user-id/{userId}")
    public ResponseEntity<List<ProjectDto>> getProjectsForUserId(@PathVariable Long userId) {
        log.info("Fetching projects for user ID: {}", userId);
        List<ProjectDto> projects = projectService.getProjectsForUserId(userId);
        return ResponseEntity.ok(projects);
    }

    /**
     * Get project by ID
     * GET /api/projects/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> getProjectById(@PathVariable Long id) {
        log.info("Fetching project with ID: {}", id);
        Optional<ProjectDto> project = projectService.getProjectById(id);
        
        if (project.isPresent()) {
            return ResponseEntity.ok(project.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Create new project
     * POST /api/projects
     */
    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@Valid @RequestBody ProjectCreateRequest request) {
        log.info("Creating new project: {}", request.getName());
        
        try {
            ProjectDto createdProject = projectService.createProject(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
        } catch (Exception e) {
            log.error("Error creating project: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Update existing project
     * PUT /api/projects/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProjectDto> updateProject(@PathVariable Long id, 
                                                   @Valid @RequestBody ProjectUpdateRequest request) {
        log.info("Updating project: {}", id);
        
        try {
            // Ensure the ID matches
            request.setId(id);
            ProjectDto updatedProject = projectService.updateProject(id, request);
            return ResponseEntity.ok(updatedProject);
        } catch (RuntimeException e) {
            log.error("Error updating project {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error updating project {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Delete project
     * DELETE /api/projects/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        log.info("Deleting project: {}", id);
        
        try {
            projectService.deleteProject(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Error deleting project {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error deleting project {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Add user to project
     * POST /api/projects/{projectId}/members/{userId}
     */
    @PostMapping("/{projectId}/members/{userId}")
    public ResponseEntity<ProjectDto> addUserToProject(@PathVariable Long projectId, 
                                                      @PathVariable Long userId) {
        log.info("Adding user {} to project {}", userId, projectId);
        
        try {
            ProjectDto updatedProject = projectService.addUserToProject(projectId, userId);
            return ResponseEntity.ok(updatedProject);
        } catch (RuntimeException e) {
            log.error("Error adding user to project: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Remove user from project
     * DELETE /api/projects/{projectId}/members/{userId}
     */
    @DeleteMapping("/{projectId}/members/{userId}")
    public ResponseEntity<ProjectDto> removeUserFromProject(@PathVariable Long projectId, 
                                                           @PathVariable Long userId) {
        log.info("Removing user {} from project {}", userId, projectId);
        
        try {
            ProjectDto updatedProject = projectService.removeUserFromProject(projectId, userId);
            return ResponseEntity.ok(updatedProject);
        } catch (RuntimeException e) {
            log.error("Error removing user from project: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Search projects by keyword
     * GET /api/projects/search?q={keyword}
     */
    @GetMapping("/search")
    public ResponseEntity<List<ProjectDto>> searchProjects(@RequestParam("q") String keyword) {
        log.info("Searching projects with keyword: {}", keyword);
        List<ProjectDto> projects = projectService.searchProjects(keyword);
        return ResponseEntity.ok(projects);
    }
}

