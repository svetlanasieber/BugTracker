package com.bugtracker.web.api;

import com.bugtracker.client.ProjectClient;
import com.bugtracker.client.dto.ProjectCreateRequest;
import com.bugtracker.client.dto.ProjectDto;
import com.bugtracker.client.dto.ProjectUpdateRequest;
import com.bugtracker.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Validated
public class ApiProjectController {

    private final ProjectClient projectClient;
    private final AuthService authService;

    @GetMapping
    public ResponseEntity<List<ProjectDto>> getProjectsForCurrentUser(Principal principal) {
        List<ProjectDto> projects = projectClient.getProjectsForUser(principal.getName());
        return ResponseEntity.ok(projects);
    }

    
    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@Valid @RequestBody ProjectCreateRequest request, Principal principal) {
        try {
            
            Long currentUserId = authService.getCurrentUserId();
            request.setCreatedByUserId(currentUserId);
            request.setCreatedByUsername(principal.getName());
            
            ProjectDto createdProject = projectClient.createProject(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    
    @PutMapping("/{id}")
    public ResponseEntity<ProjectDto> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectUpdateRequest request,
            Principal principal) {
        try {
            
            request.setId(id);
            
            
            
            
            ProjectDto updatedProject = projectClient.updateProject(id, request);
            return ResponseEntity.ok(updatedProject);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id, Principal principal) {
        try {
            
            
            
            projectClient.deleteProject(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
