package com.bugtracker.web.api;

import com.bugtracker.client.dto.ProjectCreateRequest;
import com.bugtracker.client.dto.ProjectDto;
import com.bugtracker.client.dto.ProjectUpdateRequest;
import com.bugtracker.service.ProjectApiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ApiProjectController {

    private final ProjectApiService projectApiService;

    @GetMapping
    public ResponseEntity<List<ProjectDto>> getProjectsForCurrentUser(Principal principal) {
        List<ProjectDto> projects = projectApiService.getProjectsForUser(principal.getName());
        return ResponseEntity.ok(projects);
    }

    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@Valid @RequestBody ProjectCreateRequest request, Principal principal) {
        ProjectDto createdProject = projectApiService.createProject(request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectDto> updateProject(
            @PathVariable UUID id,
            @Valid @RequestBody ProjectUpdateRequest request) {
        
        ProjectDto updatedProject = projectApiService.updateProject(id, request);
        return ResponseEntity.ok(updatedProject);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID id) {
        projectApiService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}
