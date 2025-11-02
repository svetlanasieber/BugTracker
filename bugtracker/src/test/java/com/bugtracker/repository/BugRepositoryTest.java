package com.bugtracker.repository;

import com.bugtracker.bug.model.Bug;
import com.bugtracker.bug.repository.BugRepository;
import com.bugtracker.model.enums.BugPriority;
import com.bugtracker.model.enums.BugStatus;
import com.bugtracker.project.model.Project;
import com.bugtracker.user.model.Role;
import com.bugtracker.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BugRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BugRepository bugRepository;

    private User testUser;
    private Project testProject;

    @BeforeEach
    void setUp() {
       
        Role role = Role.builder()
                .name("DEVELOPER")
                .build();
        entityManager.persist(role);

       
        testUser = User.builder()
                .email("developer@bugtracker.com")
                .firstName("Dev")
                .lastName("User")
                .password("password")
                .roles(Set.of(role))
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        entityManager.persist(testUser);

      
        testProject = Project.builder()
                .id(UUID.randomUUID())
                .name("Test Project")
                .description("Test Description")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        entityManager.persist(testProject);
        entityManager.flush();
    }

    @Test
    void findByProject_Id_WithExistingBugs_ReturnsBugs() {
        // Given
        Bug bug = Bug.builder()
                .id(UUID.randomUUID())
                .title("Test Bug")
                .description("Bug Description")
                .status(BugStatus.OPEN)
                .priority(BugPriority.HIGH)
                .project(testProject)
                .reporter(testUser)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        entityManager.persist(bug);
        entityManager.flush();

        // When
        List<Bug> bugs = bugRepository.findByProject_Id(testProject.getId());

        // Then
        assertThat(bugs).hasSize(1);
        assertThat(bugs.get(0).getTitle()).isEqualTo("Test Bug");
        assertThat(bugs.get(0).getProject().getId()).isEqualTo(testProject.getId());
    }

    @Test
    void findByStatus_WithOpenStatus_ReturnsOpenBugs() {
        // Given
        Bug openBug = Bug.builder()
                .id(UUID.randomUUID())
                .title("Open Bug")
                .description("Description")
                .status(BugStatus.OPEN)
                .priority(BugPriority.MEDIUM)
                .project(testProject)
                .reporter(testUser)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        entityManager.persist(openBug);

        Bug closedBug = Bug.builder()
                .id(UUID.randomUUID())
                .title("Closed Bug")
                .description("Description")
                .status(BugStatus.CLOSED)
                .priority(BugPriority.LOW)
                .project(testProject)
                .reporter(testUser)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        entityManager.persist(closedBug);
        entityManager.flush();

        // When
        List<Bug> openBugs = bugRepository.findByStatus(BugStatus.OPEN);

        // Then
        assertThat(openBugs).hasSize(1);
        assertThat(openBugs.get(0).getStatus()).isEqualTo(BugStatus.OPEN);
    }

    @Test
    void findByPriority_WithHighPriority_ReturnsHighPriorityBugs() {
        // Given
        Bug highPriorityBug = Bug.builder()
                .id(UUID.randomUUID())
                .title("Critical Bug")
                .description("Description")
                .status(BugStatus.OPEN)
                .priority(BugPriority.HIGH)
                .project(testProject)
                .reporter(testUser)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        entityManager.persist(highPriorityBug);
        entityManager.flush();

        // When
        List<Bug> highPriorityBugs = bugRepository.findByPriority(BugPriority.HIGH);

        // Then
        assertThat(highPriorityBugs).hasSize(1);
        assertThat(highPriorityBugs.get(0).getPriority()).isEqualTo(BugPriority.HIGH);
    }

    @Test
    void countByAssignedTo_Id_WithAssignedBugs_ReturnsCount() {
        // Given
        Bug assignedBug = Bug.builder()
                .id(UUID.randomUUID())
                .title("Assigned Bug")
                .description("Description")
                .status(BugStatus.IN_PROGRESS)
                .priority(BugPriority.MEDIUM)
                .project(testProject)
                .reporter(testUser)
                .assignedTo(testUser)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        entityManager.persist(assignedBug);
        entityManager.flush();

        // When
        Long count = bugRepository.countByAssignedTo_Id(testUser.getId());

        // Then
        assertThat(count).isEqualTo(1L);
    }

    @Test
    void searchByKeyword_WithMatchingKeyword_ReturnsBugs() {
        // Given
        Bug bug1 = Bug.builder()
                .id(UUID.randomUUID())
                .title("Login Bug")
                .description("User cannot login")
                .status(BugStatus.OPEN)
                .priority(BugPriority.HIGH)
                .project(testProject)
                .reporter(testUser)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        entityManager.persist(bug1);

        Bug bug2 = Bug.builder()
                .id(UUID.randomUUID())
                .title("Database Error")
                .description("Connection timeout")
                .status(BugStatus.OPEN)
                .priority(BugPriority.MEDIUM)
                .project(testProject)
                .reporter(testUser)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        entityManager.persist(bug2);
        entityManager.flush();

        // When
        Page<Bug> results = bugRepository.searchByKeyword("login", PageRequest.of(0, 10));

        // Then
        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getContent().get(0).getTitle()).contains("Login");
    }
}

