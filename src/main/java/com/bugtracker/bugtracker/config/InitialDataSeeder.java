package com.bugtracker.bugtracker.config;

import com.bugtracker.bugtracker.user.model.Role;
import com.bugtracker.bugtracker.user.model.User;
import com.bugtracker.bugtracker.user.repository.RoleRepository;
import com.bugtracker.bugtracker.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(1) 
public class InitialDataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("Starting BugTracker Initial Data Seeding...");
        
        seedRoles();
        seedProductionUsers();
        seedDevelopmentUsers();
        
        log.info("BugTracker Initial Data Seeding completed!");
    }

    private void seedRoles() {
        if (roleRepository.count() > 0) {
            log.info("Roles already exist, ensuring all required roles are present...");
        }
        
        
        List<String> roleNames = List.of("ADMIN", "DEVELOPER", "QA", "PROJECT_MANAGER", "USER");
        
        for (String roleName : roleNames) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                Role role = Role.builder()
                        .name(roleName)
                        .build();
                roleRepository.save(role);
                log.info("Created role: {}", roleName);
            }
        }
    }

    private void seedProductionUsers() {
        log.info("Seeding production users...");
        
        String adminEmail = "admin@bugtracker.com";
        String adminPassword = "Admin123!";
        
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            log.info("Creating production admin user...");
            
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new RuntimeException("ADMIN role not found"));
            
            User admin = User.builder()
                    .firstName("Admin")
                    .lastName("User")
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .roles(Set.of(adminRole))
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .isActive(true)
                    .build();

            userRepository.save(admin);
            
            log.info("Production admin created: {}", adminEmail);
        } else {
            
            User existingAdmin = userRepository.findByEmail(adminEmail).get();
            existingAdmin.setPassword(passwordEncoder.encode(adminPassword));
            userRepository.save(existingAdmin);
            log.info("Updated admin password");
            log.info("Updated admin user: {}", adminEmail);
        }
    }
    
    private void seedDevelopmentUsers() {
        log.info("👥 Seeding development users...");
        

        //test role
        /*createUserIfNotExists("developer@bugtracker.com", "Dev123!", "John", "Developer", "DEVELOPER");
        createUserIfNotExists("qa@bugtracker.com", "QA123!", "Jane", "Tester", "QA");
        createUserIfNotExists("pm@bugtracker.com", "PM123!", "Bob", "Manager", "PROJECT_MANAGER");
        createUserIfNotExists("user@bugtracker.com", "User123!", "Alice", "User", "USER");
        
        log.info("Development users created successfully!");
        log.info("Development Login Credentials:");
        log.info("Developer: developer@bugtracker.com / Dev123!");
        log.info("QA Engineer: qa@bugtracker.com / QA123!");
        log.info("Project Manager: pm@bugtracker.com / PM123!");
        log.info("Basic User: user@bugtracker.com / User123!");*/
    }
    
    private void createUserIfNotExists(String email, String password, String firstName, String lastName, String roleName) {
        if (userRepository.findByEmail(email).isEmpty()) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
            
            User user = User.builder()
                    .firstName(firstName)
                    .lastName(lastName)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .roles(Set.of(role))
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .isActive(true)
                    .build();

            userRepository.save(user);
            log.info("Created user: {} with role: {}", email, roleName);
        }
    }
} 
