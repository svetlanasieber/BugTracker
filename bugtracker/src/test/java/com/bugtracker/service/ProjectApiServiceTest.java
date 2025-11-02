package com.bugtracker.service;

import com.bugtracker.client.ProjectClient;
import com.bugtracker.client.dto.ProjectCreateRequest;
import com.bugtracker.client.dto.ProjectDto;
import com.bugtracker.client.dto.ProjectUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectApiServiceTest {

    @Mock
    private ProjectClient projectClient;

    @Mock
    private AuthService authService;

    @InjectMocks
    private ProjectApiService projectApiService;

    private ProjectDto testProjectDto;
    private UUID testProjectId;

    @BeforeEach
    void setUp() {
        testProjectId = UUID.randomUUID();
        testProjectDto = ProjectDto.builder()
                .id(testProjectId)
                .name("Test Project")
                .description("Test Description")
                .createdByUsername("test@bugtracker.com")
                .createdByUserId(1L)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getProjectsForUser_WithValidUsername_ReturnsProjects() {
        // Given
        List<ProjectDto> expectedProjects = List.of(testProjectDto);
        when(projectClient.getProjectsForUser("test@bugtracker.com")).thenReturn(expectedProjects);

        // When
        List<ProjectDto> result = projectApiService.getProjectsForUser("test@bugtracker.com");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Project");
        verify(projectClient).getProjectsForUser("test@bugtracker.com");
    }

    @Test
    void createProject_WithValidRequest_ReturnsCreatedProject() {
        // Given
        ProjectCreateRequest request = new ProjectCreateRequest();
        request.setName("New Project");
        request.setDescription("New Description");

        when(authService.getCurrentUserId()).thenReturn(1L);
        when(projectClient.createProject(any(ProjectCreateRequest.class))).thenReturn(testProjectDto);

        // When
        ProjectDto result = projectApiService.createProject(request, "test@bugtracker.com");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Project");
        verify(authService).getCurrentUserId();
        verify(projectClient).createProject(request);
        
        // Verify that user info was set on request
        assertThat(request.getCreatedByUserId()).isEqualTo(1L);
        assertThat(request.getCreatedByUsername()).isEqualTo("test@bugtracker.com");
    }

    @Test
    void updateProject_WithValidRequest_ReturnsUpdatedProject() {
        // Given
        ProjectUpdateRequest updateRequest = new ProjectUpdateRequest();
        updateRequest.setName("Updated Project");
        updateRequest.setDescription("Updated Description");

        when(projectClient.updateProject(eq(testProjectId), any(ProjectUpdateRequest.class)))
                .thenReturn(testProjectDto);

        // When
        ProjectDto result = projectApiService.updateProject(testProjectId, updateRequest);

        // Then
        assertThat(result).isNotNull();
        verify(projectClient).updateProject(testProjectId, updateRequest);
    }

    @Test
    void deleteProject_WithValidId_CallsClientDelete() {
        // Given
        doNothing().when(projectClient).deleteProject(testProjectId);

        // When
        projectApiService.deleteProject(testProjectId);

        // Then
        verify(projectClient).deleteProject(testProjectId);
    }
}

