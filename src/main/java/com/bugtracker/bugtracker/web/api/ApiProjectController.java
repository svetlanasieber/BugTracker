package com.bugtracker.bugtracker.web.api;

import com.bugtracker.bugtracker.project.model.Project;
import com.bugtracker.bugtracker.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ApiProjectController {

    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<List<Project>> getProjectsForCurrentUser(Principal principal) {
        List<Project> projects = projectService.getProjectsForUser(principal.getName());
        return ResponseEntity.ok(projects);
    }
}
