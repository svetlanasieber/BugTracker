package com.bugtracker.client;

import com.bugtracker.client.dto.ProjectCreateRequest;
import com.bugtracker.client.dto.ProjectDto;
import com.bugtracker.client.dto.ProjectUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "project-service", url = "http://localhost:8081/api/projects")
public interface ProjectClient {

    @GetMapping
    List<ProjectDto> getAllProjects();

    @GetMapping("/user/{username}")
    List<ProjectDto> getProjectsForUser(@PathVariable("username") String username);

    @GetMapping("/user-id/{userId}")
    List<ProjectDto> getProjectsForUserId(@PathVariable("userId") Long userId);

    @GetMapping("/{id}")
    ProjectDto getProjectById(@PathVariable("id") UUID id);

    @PostMapping
    ProjectDto createProject(@RequestBody ProjectCreateRequest request);

    @PutMapping("/{id}")
    ProjectDto updateProject(@PathVariable("id") UUID id, @RequestBody ProjectUpdateRequest request);

    @DeleteMapping("/{id}")
    void deleteProject(@PathVariable("id") UUID id);

    @PostMapping("/{projectId}/members/{userId}")
    ProjectDto addUserToProject(@PathVariable("projectId") UUID projectId, @PathVariable("userId") Long userId);

    @DeleteMapping("/{projectId}/members/{userId}")
    ProjectDto removeUserFromProject(@PathVariable("projectId") UUID projectId, @PathVariable("userId") Long userId);

    @GetMapping("/search")
    List<ProjectDto> searchProjects(@RequestParam("q") String keyword);
}

