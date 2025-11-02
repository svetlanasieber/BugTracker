package com.bugtracker.service;

import com.bugtracker.service.impl.AuthServiceImpl;
import com.bugtracker.user.model.Role;
import com.bugtracker.user.model.User;
import com.bugtracker.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        // Create test user
        Role adminRole = Role.builder()
                .id(1L)
                .name("ADMIN")
                .build();

        testUser = User.builder()
                .id(1L)
                .email("test@bugtracker.com")
                .firstName("Test")
                .lastName("User")
                .roles(Set.of(adminRole))
                .isActive(true)
                .build();

        // Setup authentication
        authentication = new UsernamePasswordAuthenticationToken(
                "test@bugtracker.com",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getCurrentUsername_WithAuthenticatedUser_ReturnsUsername() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // When
        String username = authService.getCurrentUsername();

        // Then
        assertThat(username).isEqualTo("test@bugtracker.com");
    }

    @Test
    void getCurrentUsername_WithNoAuthentication_ThrowsException() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> authService.getCurrentUsername())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No authenticated user found");
    }

    @Test
    void getCurrentUsername_WithAnonymousUser_ThrowsException() {
        // Given
        Authentication anonymousAuth = new UsernamePasswordAuthenticationToken(
                "anonymousUser", null, Collections.emptyList()
        );
        when(securityContext.getAuthentication()).thenReturn(anonymousAuth);

        // When & Then
        assertThatThrownBy(() -> authService.getCurrentUsername())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No authenticated user found");
    }

    @Test
    void getCurrentUserId_WithValidUser_ReturnsUserId() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userRepository.findByEmail("test@bugtracker.com")).thenReturn(Optional.of(testUser));

        // When
        Long userId = authService.getCurrentUserId();

        // Then
        assertThat(userId).isEqualTo(1L);
        verify(userRepository).findByEmail("test@bugtracker.com");
    }

    @Test
    void getCurrentUserId_WithNonExistentUser_ThrowsException() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userRepository.findByEmail("test@bugtracker.com")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authService.getCurrentUserId())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found for username");
    }

    @Test
    void isCurrentUserAdmin_WithAdminRole_ReturnsTrue() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // When
        boolean isAdmin = authService.isCurrentUserAdmin();

        // Then
        assertThat(isAdmin).isTrue();
    }

    @Test
    void isCurrentUserAdmin_WithoutAdminRole_ReturnsFalse() {
        // Given
        Authentication userAuth = new UsernamePasswordAuthenticationToken(
                "user@bugtracker.com",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        when(securityContext.getAuthentication()).thenReturn(userAuth);

        // When
        boolean isAdmin = authService.isCurrentUserAdmin();

        // Then
        assertThat(isAdmin).isFalse();
    }
}

