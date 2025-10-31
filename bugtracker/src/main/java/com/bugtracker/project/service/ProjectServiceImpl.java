package com.bugtracker.project.service;

import com.bugtracker.model.entity.LogEntry;
import com.bugtracker.project.model.Project;
import com.bugtracker.user.model.User;
import com.bugtracker.user.model.Role;
import com.bugtracker.model.enums.LogLevel;
import com.bugtracker.project.repository.ProjectRepository;
import com.bugtracker.user.repository.UserRepository;
import com.bugtracker.user.repository.RoleRepository;
import com.bugtracker.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.bugtracker.web.dto.ProjectAdd;
import com.bugtracker.web.dto.ProjectUpdate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final DataSource dataSource;
    private final LogService logService;

    @Override
    @Transactional
    public Project createProject(Project project) {
        
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());
        
        
        if (project.isActive() == false) {
            project.setActive(true);
        }
        
        Project savedProject = projectRepository.save(project);
        
        
        logService.createLogEntry(
                "CREATE",
                "Project",
                savedProject.getId(),
                null, 
                "Project created: " + savedProject.getName(),
                LogLevel.INFO
        );
        
        log.info("Created new project with ID: {}", savedProject.getId());
        return savedProject;
    }

    @Override
    @Transactional
    public Project createProject(String name, String description) {
        Project project = Project.builder()
                .name(name)
                .description(description)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isActive(true)
                .build();
                
        return createProject(project);
    }

    @Override
    public Optional<Project> findById(Long id) {
        return projectRepository.findById(id);
    }

    @Override
    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    @Override
    public List<Project> findProjectsByUserId(Long userId) {
        return projectRepository.findByMemberId(userId);
    }

    @Override
    @Transactional
    public Project updateProject(Project project) {
        
        Project existingProject = projectRepository.findById(project.getId())
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + project.getId()));
        
        
        project.setUpdatedAt(LocalDateTime.now());
        Project updatedProject = projectRepository.save(project);
        
        
        logService.createLogEntry(
                "UPDATE",
                "Project",
                updatedProject.getId(),
                null,
                "Project updated: " + updatedProject.getName(),
                LogLevel.INFO
        );
        
        log.info("Updated project with ID: {}", updatedProject.getId());
        return updatedProject;
    }

    @Override
    @Transactional
    public Project updateProject(Long id, String name, String description) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
        
        project.setName(name);
        project.setDescription(description);
        project.setUpdatedAt(LocalDateTime.now());
        
        return updateProject(project);
    }

    @Override
    @Transactional
    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
        
        projectRepository.delete(project);
        
        
        logService.createLogEntry(
                "DELETE",
                "Project",
                id,
                null,
                "Project deleted: " + project.getName(),
                LogLevel.WARNING
        );
        
        log.info("Deleted project with ID: {}", id);
    }

    @Override
    @Transactional
    public void addUserToProject(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));
                
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
                
        project.getMembers().add(user);
        projectRepository.save(project);
        
        
        logService.createLogEntry(
                "ADD_MEMBER",
                "Project",
                projectId,
                userId,
                "User added to project: " + user.getEmail(),
                LogLevel.INFO
        );
        
        log.info("Added user with ID: {} to project with ID: {}", userId, projectId);
    }

    @Override
    @Transactional
    public void removeUserFromProject(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));
                
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
                
        project.getMembers().remove(user);
        projectRepository.save(project);
        
        
        logService.createLogEntry(
                "REMOVE_MEMBER",
                "Project",
                projectId,
                userId,
                "User removed from project: " + user.getEmail(),
                LogLevel.INFO
        );
        
        log.info("Removed user with ID: {} from project with ID: {}", userId, projectId);
    }

    @Override
    public long countUserProjects(Long userId) {
        return projectRepository.countByMemberId(userId);
    }
    
    
    
    @Override
    @Transactional
    public Project changeProjectStatus(Long projectId, boolean isActive) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));
        
        project.setActive(isActive);
        project.setUpdatedAt(LocalDateTime.now());
        
        Project updatedProject = projectRepository.save(project);
        
        
        logService.createLogEntry(
                "STATUS_CHANGE",
                "Project",
                projectId,
                null,
                "Project status changed to " + (isActive ? "active" : "inactive"),
                LogLevel.INFO
        );
        
        log.info("Changed project with ID: {} status to {}", projectId, isActive ? "active" : "inactive");
        return updatedProject;
    }
    
    @Override
    public List<Project> searchProjects(String keyword) {
        return projectRepository.searchByKeyword(keyword);
    }
    
    
    
    @Override
    public List<Project> getAllProjects() {
        return findAll();
    }
    
    @Override
    public Project getProjectById(Long id) {
        return findById(id)
            .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
    }
    
    @Override
    public List<Project> getProjectsByUserId(Long userId) {
        return findProjectsByUserId(userId);
    }
    
    @Override
    @Transactional
    public void assignUserToProject(Long userId, Long projectId) {
        addUserToProject(projectId, userId);
    }
    
    @Override
    @Transactional
    public void removeAllUsersFromProject(Long projectId) {
        Project project = getProjectById(projectId);
        project.getMembers().clear();
        projectRepository.save(project);
        
        logService.createLogEntry(
                "REMOVE_ALL_MEMBERS",
                "Project",
                projectId,
                null,
                "All users removed from project",
                LogLevel.INFO
        );
        
        log.info("Removed all users from project with ID: {}", projectId);
    }
    
    
    
    @Override
    public List<Project> getProjectsForUser(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + username));
        
        
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> "ADMIN".equals(role.getName()));
        
        if (isAdmin) {
            
            return getAllProjects();
        } else {
            
            return getProjectsByUserId(user.getId());
        }
    }
    
    @Override
    public boolean isUserAuthorizedForProject(String username, Long projectId) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + username));
        
        
        if (user.getRoles().stream().anyMatch(role -> "ADMIN".equals(role.getName()))) {
            return true;
        }
        
        
        Project project = getProjectById(projectId);
        return project.getMembers().stream()
                .anyMatch(projectUser -> projectUser.getId().equals(user.getId()));
    }
    
    @Override
    @Transactional
    public Project createProjectWithUsers(String name, String description, List<Long> userIds) {
        
        Project project = createProject(name, description);
        
        
        if (userIds != null && !userIds.isEmpty()) {
            for (Long userId : userIds) {
                try {
                    assignUserToProject(userId, project.getId());
                } catch (Exception e) {
                    log.warn("Could not assign user {} to project {}: {}", userId, project.getId(), e.getMessage());
                }
            }
        }
        
        return getProjectById(project.getId()); 
    }
    
    @Override
    @Transactional
    public Project updateProjectWithUsers(Long projectId, String name, String description, List<Long> userIds) {
        
        Project project = updateProject(projectId, name, description);
        
        
        removeAllUsersFromProject(projectId);
        
        
        if (userIds != null && !userIds.isEmpty()) {
            for (Long userId : userIds) {
                try {
                    assignUserToProject(userId, projectId);
                } catch (Exception e) {
                    log.warn("Could not assign user {} to project {}: {}", userId, projectId, e.getMessage());
                }
            }
        }
        
        return getProjectById(projectId); 
    }
    
    @Override
    @Transactional
    public String performProjectDatabaseFix() {
        try {
            
            try (Connection connection = dataSource.getConnection()) {
                connection.setAutoCommit(false);
                
                try (Statement statement = connection.createStatement()) {
                    
                    statement.execute("INSERT IGNORE INTO users (first_name, last_name, email, password, created_at, updated_at, is_active) " +
                                     "VALUES ('Admin', 'User', 'admin@bugtracker.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', NOW(), NOW(), TRUE)");
                    
                    
                    statement.execute("INSERT IGNORE INTO roles (name) VALUES ('ROLE_USER')");
                    statement.execute("INSERT IGNORE INTO roles (name) VALUES ('ROLE_ADMIN')");
                    
                    
                    statement.execute("INSERT IGNORE INTO users_roles (user_id, role_id) " +
                                     "SELECT (SELECT id FROM users WHERE email = 'admin@bugtracker.com'), (SELECT id FROM roles WHERE name = 'ROLE_ADMIN')");
                    
                    
                    statement.execute("INSERT INTO projects (name, description, is_active, created_at, updated_at) " +
                                     "SELECT 'Bug Tracker Development', 'Internal project for developing the bug tracking application', TRUE, NOW(), NOW() " +
                                     "WHERE NOT EXISTS (SELECT 1 FROM projects LIMIT 1)");
                    
                    
                    statement.execute("INSERT IGNORE INTO project_members (project_id, user_id) " +
                                     "SELECT (SELECT MAX(id) FROM projects), (SELECT id FROM users WHERE email = 'admin@bugtracker.com')");
                    
                    connection.commit();
                    
                    
                    logService.createLogEntry(
                            "DATABASE_FIX",
                            "Project",
                            null,
                            null,
                            "Database fix operation completed successfully",
                            LogLevel.INFO
                    );
                    
                    return "Projects fixed successfully! Please refresh the page.";
                    
                } catch (Exception e) {
                    connection.rollback();
                    throw e;
                }
            }
        } catch (Exception e) {
            log.error("Error performing database fix: {}", e.getMessage(), e);
            return "Error fixing projects: " + e.getMessage();
        }
    }
    
    
    
    @Override
    @Transactional
    public Project createProjectFromDTO(ProjectAdd projectAdd) {
        return createProjectWithUsers(
            projectAdd.getName(),
            projectAdd.getDescription(),
            projectAdd.getMemberIds()
        );
    }
    
    @Override
    @Transactional
    public Project updateProjectFromDTO(ProjectUpdate projectUpdate) {
        return updateProjectWithUsers(
            projectUpdate.getId(),
            projectUpdate.getName(),
            projectUpdate.getDescription(),
            projectUpdate.getMemberIds()
        );
    }
} 