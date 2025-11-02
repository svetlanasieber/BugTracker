package com.bugtracker.repository;

import com.bugtracker.user.model.Role;
import com.bugtracker.user.model.User;
import com.bugtracker.user.repository.RoleRepository;
import com.bugtracker.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Role testRole;

    @BeforeEach
    void setUp() {
        testRole = Role.builder()
                .name("USER")
                .build();
        entityManager.persist(testRole);
        entityManager.flush();
    }

    @Test
    void findByEmail_WithExistingEmail_ReturnsUser() {
        // Given
        User user = User.builder()
                .email("test@bugtracker.com")
                .firstName("Test")
                .lastName("User")
                .password("encodedPassword")
                .roles(Set.of(testRole))
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        entityManager.persist(user);
        entityManager.flush();

        // When
        Optional<User> found = userRepository.findByEmail("test@bugtracker.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@bugtracker.com");
        assertThat(found.get().getFirstName()).isEqualTo("Test");
    }

    @Test
    void findByEmail_WithNonExistingEmail_ReturnsEmpty() {
        // When
        Optional<User> found = userRepository.findByEmail("nonexistent@bugtracker.com");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void existsByEmail_WithExistingEmail_ReturnsTrue() {
        // Given
        User user = User.builder()
                .email("existing@bugtracker.com")
                .firstName("Existing")
                .lastName("User")
                .password("encodedPassword")
                .roles(Set.of(testRole))
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        entityManager.persist(user);
        entityManager.flush();

        // When
        boolean exists = userRepository.existsByEmail("existing@bugtracker.com");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmail_WithNonExistingEmail_ReturnsFalse() {
        // When
        boolean exists = userRepository.existsByEmail("nonexistent@bugtracker.com");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void findByUsername_WithExistingUsername_ReturnsUser() {
        // Given
        User user = User.builder()
                .email("user@bugtracker.com")
                .username("testuser")
                .firstName("Test")
                .lastName("User")
                .password("encodedPassword")
                .roles(Set.of(testRole))
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        entityManager.persist(user);
        entityManager.flush();

        // When
        Optional<User> found = userRepository.findByUsername("testuser");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    void save_WithValidUser_PersistsUser() {
        // Given
        User newUser = User.builder()
                .email("newuser@bugtracker.com")
                .firstName("New")
                .lastName("User")
                .password("encodedPassword")
                .roles(Set.of(testRole))
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // When
        User savedUser = userRepository.save(newUser);
        entityManager.flush();

        // Then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("newuser@bugtracker.com");
        
        Optional<User> retrieved = userRepository.findById(savedUser.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getEmail()).isEqualTo("newuser@bugtracker.com");
    }
}

