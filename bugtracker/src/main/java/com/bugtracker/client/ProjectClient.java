package com.bugtracker.client;

import com.bugtracker.client.dto.ProjectCreateRequest;
import com.bugtracker.client.dto.ProjectDto;
import com.bugtracker.client.dto.ProjectUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "project-service", url = "http:
public interface ProjectClient {

    @GetMapping
    List<ProjectDto> getAllProjects();

    @GetMapping("/user/{username}")
    List<ProjectDto> getProjectsForUser(@PathVariable("username") String username);

    @GetMapping("/user-id/{userId}")
    List<ProjectDto> getProjectsForUserId(@PathVariable("userId") Long userId);

    @GetMapping("/{id}")
    ProjectDto getProjectById(@PathVariable("id") Long id);

    @PostMapping
    ProjectDto createProject(@RequestBody ProjectCreateRequest request);

    @PutMapping("/{id}")
    ProjectDto updateProject(@PathVariable("id") Long id, @RequestBody ProjectUpdateRequest request);

    @DeleteMapping("/{id}")
    void deleteProject(@PathVariable("id") Long id);

    @PostMapping("/{projectId}/members/{userId}")
    ProjectDto addUserToProject(@PathVariable("projectId") Long projectId, @PathVariable("userId") Long userId);

    @DeleteMapping("/{projectId}/members/{userId}")
    ProjectDto removeUserFromProject(@PathVariable("projectId") Long projectId, @PathVariable("userId") Long userId);

    @GetMapping("/search")
    List<ProjectDto> searchProjects(@RequestParam("q") String keyword);
}

