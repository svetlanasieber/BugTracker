package com.bugtracker.project.service;

import com.bugtracker.bug.model.Bug;
import com.bugtracker.bug.repository.BugRepository;
import com.bugtracker.client.ProjectClient;
import com.bugtracker.client.dto.ProjectCreateRequest;
import com.bugtracker.client.dto.ProjectDto;
import com.bugtracker.client.dto.ProjectUpdateRequest;
import com.bugtracker.project.model.Project;
import com.bugtracker.user.model.User;
import com.bugtracker.user.repository.UserRepository;
import com.bugtracker.web.dto.ProjectAdd;
import com.bugtracker.web.dto.ProjectUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectServiceAdapter implements ProjectService {

    private final ProjectClient projectClient;
    private final UserRepository userRepository;
    private final BugRepository bugRepository;

    @Override
    public Project createProject(Project project) {
        log.info("Creating project via microservice: {}", project.getName());
        
        ProjectCreateRequest request = ProjectCreateRequest.builder()
                .name(project.getName())
                .description(project.getDescription())
                .projectType(project.getProjectType())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .isActive(project.isActive())
                .memberIds(project.getMembers() != null ? 
                    project.getMembers().stream().map(User::getId).collect(Collectors.toSet()) : 
                    new HashSet<>())
                .build();
        
        ProjectDto dto = projectClient.createProject(request);
        return convertToEntity(dto);
    }

    @Override
    public Project createProject(String name, String description) {
        log.info("Creating simple project via microservice: {}", name);
        
        ProjectCreateRequest request = ProjectCreateRequest.builder()
                .name(name)
                .description(description)
                .isActive(true)
                .memberIds(new HashSet<>())
                .build();
        
        ProjectDto dto = projectClient.createProject(request);
        return convertToEntity(dto);
    }

    @Override
    public Optional<Project> findById(UUID id) {
        try {
            ProjectDto dto = projectClient.getProjectById(id);
            return Optional.of(convertToEntity(dto));
        } catch (Exception e) {
            log.error("Error finding project by ID: {}", id, e);
            return Optional.empty();
        }
    }

    @Override
    public List<Project> findAll() {
        List<ProjectDto> dtos = projectClient.getAllProjects();
        return dtos.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Project> findProjectsByUserId(Long userId) {
        List<ProjectDto> dtos = projectClient.getProjectsForUserId(userId);
        return dtos.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Project updateProject(Project project) {
        log.info("Updating project via microservice: {}", project.getId());
        
        ProjectUpdateRequest request = ProjectUpdateRequest.builder()
                .name(project.getName())
                .description(project.getDescription())
                .projectType(project.getProjectType())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .isActive(project.isActive())
                .memberIds(project.getMembers() != null ? 
                    project.getMembers().stream().map(User::getId).collect(Collectors.toSet()) : 
                    new HashSet<>())
                .build();
        
        ProjectDto dto = projectClient.updateProject(project.getId(), request);
        return convertToEntity(dto);
    }

    @Override
    public Project updateProject(UUID id, String name, String description) {
        log.info("Updating simple project via microservice: {}", id);
        
        // First get the existing project to preserve other fields
        ProjectDto existing = projectClient.getProjectById(id);
        
        ProjectUpdateRequest request = ProjectUpdateRequest.builder()
                .name(name)
                .description(description)
                .projectType(existing.getProjectType())
                .startDate(existing.getStartDate())
                .endDate(existing.getEndDate())
                .isActive(existing.isActive())
                .memberIds(existing.getMemberIds())
                .build();
        
        ProjectDto dto = projectClient.updateProject(id, request);
        return convertToEntity(dto);
    }

    @Override
    public void deleteProject(UUID id) {
        log.info("Deleting project via microservice: {}", id);
        projectClient.deleteProject(id);
    }

    @Override
    public void addUserToProject(UUID projectId, Long userId) {
        log.info("Adding user {} to project {}", userId, projectId);
        projectClient.addUserToProject(projectId, userId);
    }

    @Override
    public void removeUserFromProject(UUID projectId, Long userId) {
        log.info("Removing user {} from project {}", userId, projectId);
        projectClient.removeUserFromProject(projectId, userId);
    }

    @Override
    public long countUserProjects(Long userId) {
        List<ProjectDto> projects = projectClient.getProjectsForUserId(userId);
        return projects.size();
    }

    @Override
    public Project changeProjectStatus(UUID projectId, boolean isActive) {
        log.info("Changing project {} status to: {}", projectId, isActive);
        
        ProjectDto existing = projectClient.getProjectById(projectId);
        
        ProjectUpdateRequest request = ProjectUpdateRequest.builder()
                .name(existing.getName())
                .description(existing.getDescription())
                .projectType(existing.getProjectType())
                .startDate(existing.getStartDate())
                .endDate(existing.getEndDate())
                .isActive(isActive)
                .memberIds(existing.getMemberIds())
                .build();
        
        ProjectDto dto = projectClient.updateProject(projectId, request);
        return convertToEntity(dto);
    }

    @Override
    public List<Project> searchProjects(String keyword) {
        List<ProjectDto> dtos = projectClient.searchProjects(keyword);
        return dtos.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Project> getAllProjects() {
        return findAll();
    }

    @Override
    public Project getProjectById(UUID id) {
        ProjectDto dto = projectClient.getProjectById(id);
        return convertToEntity(dto);
    }

    @Override
    public List<Project> getProjectsByUserId(Long userId) {
        return findProjectsByUserId(userId);
    }

    @Override
    public void assignUserToProject(Long userId, UUID projectId) {
        addUserToProject(projectId, userId);
    }

    @Override
    public void removeAllUsersFromProject(UUID projectId) {
        log.info("Removing all users from project: {}", projectId);
        ProjectDto project = projectClient.getProjectById(projectId);
        
        if (project.getMemberIds() != null) {
            for (Long userId : project.getMemberIds()) {
                projectClient.removeUserFromProject(projectId, userId);
            }
        }
    }

    @Override
    public List<Project> getProjectsForUser(String username) {
        log.info("Getting projects for user: {}", username);
        List<ProjectDto> dtos = projectClient.getProjectsForUser(username);
        return dtos.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isUserAuthorizedForProject(String username, UUID projectId) {
        log.info("Checking authorization for user {} on project {}", username, projectId);
        try {
            ProjectDto project = projectClient.getProjectById(projectId);
            
            // Check if user is a member
            if (project.getMemberIds() != null) {
                User user = userRepository.findByUsername(username)
                        .orElse(null);
                
                if (user != null && project.getMemberIds().contains(user.getId())) {
                    return true;
                }
            }
            
            // Check if user is admin
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null && user.getRoles().stream()
                    .anyMatch(role -> "ROLE_ADMIN".equals(role.getName()))) {
                return true;
            }
            
            return false;
        } catch (Exception e) {
            log.error("Error checking authorization", e);
            return false;
        }
    }

    @Override
    public Project createProjectWithUsers(String name, String description, List<Long> userIds) {
        log.info("Creating project with users: {}", name);
        
        ProjectCreateRequest request = ProjectCreateRequest.builder()
                .name(name)
                .description(description)
                .isActive(true)
                .memberIds(userIds != null ? new HashSet<>(userIds) : new HashSet<>())
                .build();
        
        ProjectDto dto = projectClient.createProject(request);
        return convertToEntity(dto);
    }

    @Override
    public Project updateProjectWithUsers(UUID projectId, String name, String description, List<Long> userIds) {
        log.info("Updating project with users: {}", projectId);
        
        ProjectDto existing = projectClient.getProjectById(projectId);
        
        ProjectUpdateRequest request = ProjectUpdateRequest.builder()
                .name(name)
                .description(description)
                .projectType(existing.getProjectType())
                .startDate(existing.getStartDate())
                .endDate(existing.getEndDate())
                .isActive(existing.isActive())
                .memberIds(userIds != null ? new HashSet<>(userIds) : new HashSet<>())
                .build();
        
        ProjectDto dto = projectClient.updateProject(projectId, request);
        return convertToEntity(dto);
    }

    @Override
    public String performProjectDatabaseFix() {
        log.warn("Database fix endpoint called - not supported in microservice mode");
        return "Database fix is not supported when using the microservice. Please use the project-service admin endpoints.";
    }

    @Override
    public Project createProjectFromDTO(ProjectAdd projectAdd) {
        log.info("Creating project from DTO: {}", projectAdd.getName());
        
        ProjectCreateRequest request = ProjectCreateRequest.builder()
                .name(projectAdd.getName())
                .description(projectAdd.getDescription())
                .projectType(projectAdd.getProjectType())
                .startDate(projectAdd.getStartDate())
                .endDate(projectAdd.getEndDate())
                .isActive(projectAdd.isActive())
                .memberIds(projectAdd.getMemberIds() != null ? 
                    new HashSet<>(projectAdd.getMemberIds()) : 
                    new HashSet<>())
                .build();
        
        ProjectDto dto = projectClient.createProject(request);
        return convertToEntity(dto);
    }

    @Override
    public Project updateProjectFromDTO(ProjectUpdate projectUpdate) {
        log.info("Updating project from DTO: {}", projectUpdate.getId());
        
        ProjectUpdateRequest request = ProjectUpdateRequest.builder()
                .name(projectUpdate.getName())
                .description(projectUpdate.getDescription())
                .projectType(projectUpdate.getProjectType())
                .startDate(projectUpdate.getStartDate())
                .endDate(projectUpdate.getEndDate())
                .isActive(projectUpdate.isActive())
                .memberIds(projectUpdate.getMemberIds() != null ? 
                    new HashSet<>(projectUpdate.getMemberIds()) : 
                    new HashSet<>())
                .build();
        
        ProjectDto dto = projectClient.updateProject(projectUpdate.getId(), request);
        return convertToEntity(dto);
    }

    /**
     * Convert ProjectDto from microservice to Project entity for web layer
     */
    private Project convertToEntity(ProjectDto dto) {
        if (dto == null) {
            return null;
        }

        // Fetch user entities for members
        Set<User> members = new HashSet<>();
        if (dto.getMemberIds() != null && !dto.getMemberIds().isEmpty()) {
            members = dto.getMemberIds().stream()
                    .map(userId -> userRepository.findById(userId).orElse(null))
                    .filter(user -> user != null)
                    .collect(Collectors.toSet());
        }

        // Fetch bugs from local database (bugs are not managed by the microservice)
        Set<Bug> bugs = new HashSet<>();
        if (dto.getId() != null) {
            List<Bug> projectBugs = bugRepository.findByProject_Id(dto.getId());
            bugs = new HashSet<>(projectBugs);
        }

        return Project.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .projectType(dto.getProjectType())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .isActive(dto.isActive())
                .members(members)
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .bugs(bugs)
                .build();
    }
}

