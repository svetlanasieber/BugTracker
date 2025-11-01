package com.bugtracker.projectservice.service;

import com.bugtracker.projectservice.dto.ProjectCreateRequest;
import com.bugtracker.projectservice.dto.ProjectDto;
import com.bugtracker.projectservice.dto.ProjectUpdateRequest;
import com.bugtracker.projectservice.exception.ProjectNotFoundException;
import com.bugtracker.projectservice.mapper.ProjectMapper;
import com.bugtracker.projectservice.model.Project;
import com.bugtracker.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Override
    @Transactional
    public ProjectDto createProject(ProjectCreateRequest request) {
        log.info("Creating project: {} by user: {}", request.getName(), request.getCreatedByUsername());

        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .projectType(request.getProjectType())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .isActive(request.isActive())
                .createdByUserId(request.getCreatedByUserId())
                .createdByUsername(request.getCreatedByUsername())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

       
        if (request.getMemberIds() != null) {
            project.setMemberIds(request.getMemberIds());
        }

        Project savedProject = projectRepository.save(project);
        log.info("Project created successfully with ID: {}", savedProject.getId());

        return projectMapper.toDto(savedProject);
    }

    @Override
    @Transactional
    public ProjectDto updateProject(UUID id, ProjectUpdateRequest request) {
        log.info("Updating project: {}", id);

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));

        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setProjectType(request.getProjectType());
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());
        project.setActive(request.isActive());
        project.setUpdatedAt(LocalDateTime.now());

        if (request.getMemberIds() != null) {
            project.setMemberIds(request.getMemberIds());
        }

        Project savedProject = projectRepository.save(project);
        log.info("Project updated successfully: {}", savedProject.getId());

        return projectMapper.toDto(savedProject);
    }

    @Override
    @Transactional
    public void deleteProject(UUID id) {
        log.info("Deleting project: {}", id);

        if (!projectRepository.existsById(id)) {
            throw new ProjectNotFoundException(id);
        }

        projectRepository.deleteById(id);
        log.info("Project deleted successfully: {}", id);
    }

    @Override
    public ProjectDto getProjectById(UUID id) {
        return projectRepository.findById(id)
                .map(projectMapper::toDto)
                .orElseThrow(() -> new ProjectNotFoundException(id));
    }

    @Override
    public List<ProjectDto> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(projectMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectDto> getProjectsForUser(String username) {
        return projectRepository.findByCreatedByUsername(username)
                .stream()
                .map(projectMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectDto> getProjectsForUserId(Long userId) {
        
        List<Project> createdProjects = projectRepository.findByCreatedByUserId(userId);
        List<Project> memberProjects = projectRepository.findProjectsForUser(userId);

        Set<Project> allProjects = new HashSet<>(createdProjects);
        allProjects.addAll(memberProjects);

        return allProjects.stream()
                .map(projectMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isUserAuthorizedForProject(String username, UUID projectId) {
        Optional<Project> project = projectRepository.findById(projectId);
        
        if (project.isEmpty()) {
            return false;
        }

        Project proj = project.get();
        
     
        if (username.equals(proj.getCreatedByUsername())) {
            return true;
        }

        return true; 
    }

    @Override
    @Transactional
    public ProjectDto addUserToProject(UUID projectId, Long userId) {
        log.info("Adding user {} to project {}", userId, projectId);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        Set<Long> memberIds = project.getMemberIds();
        memberIds.add(userId);
        project.setMemberIds(memberIds);
        project.setUpdatedAt(LocalDateTime.now());

        Project savedProject = projectRepository.save(project);
        log.info("User {} added to project {}", userId, projectId);

        return projectMapper.toDto(savedProject);
    }

    @Override
    @Transactional
    public ProjectDto removeUserFromProject(UUID projectId, Long userId) {
        log.info("Removing user {} from project {}", userId, projectId);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        Set<Long> memberIds = project.getMemberIds();
        memberIds.remove(userId);
        project.setMemberIds(memberIds);
        project.setUpdatedAt(LocalDateTime.now());

        Project savedProject = projectRepository.save(project);
        log.info("User {} removed from project {}", userId, projectId);

        return projectMapper.toDto(savedProject);
    }

    @Override
    public List<ProjectDto> searchProjects(String keyword) {
        return projectRepository.findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(projectMapper::toDto)
                .collect(Collectors.toList());
    }
}



