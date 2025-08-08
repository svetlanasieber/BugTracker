package com.bugtracker.bugtracker.config;

import com.bugtracker.bugtracker.project.model.Project;
import com.bugtracker.bugtracker.user.model.User;
import com.bugtracker.bugtracker.project.repository.ProjectRepository;
import com.bugtracker.bugtracker.user.repository.UserRepository;
import com.bugtracker.bugtracker.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class InitialDataConfig {

    private final ProjectRepository projectRepository;
    private final ProjectService projectService;
    private final UserRepository userRepository;

    @Bean
    public CommandLineRunner initialProjectSetup() {
        return args -> {
            log.info("Checking for existing projects...");
            
            if (projectRepository.count() == 0) {
                log.info("No projects found. Creating sample project...");
                try {
                    
                    Project sampleProject = Project.builder()
                            .name("Bug Tracker Development")
                            .description("Internal project for developing and maintaining the Bug Tracker application")
                            .isActive(true)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();
                    
                    Project savedProject = projectRepository.save(sampleProject);
                    log.info("Created sample project with ID: {}", savedProject.getId());
                    
                   
                    User adminUser = userRepository.findByEmail("admin@bugtracker.com")
                            .orElseThrow(() -> new UsernameNotFoundException("Admin user not found"));
                    
                  
                    projectService.assignUserToProject(adminUser.getId(), savedProject.getId());
                    log.info("Added admin user to sample project");
                    
                } catch (Exception e) {
                    log.error("Error creating sample project: {}", e.getMessage(), e);
                }
            } else {
                log.info("Projects already exist, skipping sample project creation");
            }
        };
    }
} 
