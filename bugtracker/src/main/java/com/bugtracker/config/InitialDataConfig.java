package com.bugtracker.config;

import com.bugtracker.project.model.Project;
import com.bugtracker.user.model.User;
import com.bugtracker.user.repository.UserRepository;
import com.bugtracker.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class InitialDataConfig {

    private final ProjectService projectService;
    private final UserRepository userRepository;

    @Bean
    public CommandLineRunner initialProjectSetup() {
        return args -> {
            log.info("Checking for existing projects via microservice...");
            try {
                List<Project> allProjects = projectService.getAllProjects();
                if (allProjects.isEmpty()) {
                    log.info("No projects found. Creating sample project via microservice...");
                    try {
                        Project sampleProject = projectService.createProject(
                            "Bug Tracker Development",
                            "Internal project for developing and maintaining the Bug Tracker application"
                        );
                        log.info("Created sample project with ID: {}", sampleProject.getId());
                        try {
                            User adminUser = userRepository.findByEmail("admin@bugtracker.com")
                                    .orElse(null);
                            if (adminUser != null) {
                                projectService.assignUserToProject(adminUser.getId(), sampleProject.getId());
                                log.info("Added admin user to sample project");
                            }
                        } catch (Exception ex) {
                            log.warn("Could not assign admin to project: {}", ex.getMessage());
                        }
                    } catch (Exception e) {
                        log.error("Error creating sample project: {}", e.getMessage());
                    }
                } else {
                    log.info("Projects already exist, skipping sample project creation");
                }
            } catch (Exception e) {
                log.warn("Could not connect to project microservice: {}", e.getMessage());
                log.info("Skipping project initialization - project service may not be running yet");
            }
        };
    }
} 