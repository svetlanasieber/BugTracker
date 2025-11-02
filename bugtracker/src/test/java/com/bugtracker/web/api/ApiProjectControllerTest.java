package com.bugtracker.web.api;

import com.bugtracker.client.dto.ProjectCreateRequest;
import com.bugtracker.client.dto.ProjectDto;
import com.bugtracker.client.dto.ProjectUpdateRequest;
import com.bugtracker.service.ProjectApiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApiProjectController.class)
class ApiProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProjectApiService projectApiService;

    @Test
    @WithMockUser(username = "test@bugtracker.com")
    void getProjectsForCurrentUser_WithAuthenticatedUser_ReturnsProjects() throws Exception {
        // Given
        UUID projectId = UUID.randomUUID();
        ProjectDto projectDto = ProjectDto.builder()
                .id(projectId)
                .name("Test Project")
                .description("Test Description")
                .createdByUsername("test@bugtracker.com")
                .createdAt(LocalDateTime.now())
                .build();

        when(projectApiService.getProjectsForUser("test@bugtracker.com"))
                .thenReturn(List.of(projectDto));

        // When & Then
        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Test Project"))
                .andExpect(jsonPath("$[0].description").value("Test Description"));
    }

    @Test
    void getProjectsForCurrentUser_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test@bugtracker.com")
    void createProject_WithValidRequest_ReturnsCreated() throws Exception {
        // Given
        ProjectCreateRequest request = new ProjectCreateRequest();
        request.setName("New Project");
        request.setDescription("New Description");

        UUID projectId = UUID.randomUUID();
        ProjectDto createdProject = ProjectDto.builder()
                .id(projectId)
                .name("New Project")
                .description("New Description")
                .createdByUsername("test@bugtracker.com")
                .createdAt(LocalDateTime.now())
                .build();

        when(projectApiService.createProject(any(ProjectCreateRequest.class), eq("test@bugtracker.com")))
                .thenReturn(createdProject);

        // When & Then
        mockMvc.perform(post("/api/projects")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Project"))
                .andExpect(jsonPath("$.description").value("New Description"));

        verify(projectApiService).createProject(any(ProjectCreateRequest.class), eq("test@bugtracker.com"));
    }

    @Test
    @WithMockUser(username = "test@bugtracker.com")
    void updateProject_WithValidRequest_ReturnsOk() throws Exception {
        // Given
        UUID projectId = UUID.randomUUID();
        ProjectUpdateRequest updateRequest = new ProjectUpdateRequest();
        updateRequest.setName("Updated Project");
        updateRequest.setDescription("Updated Description");

        ProjectDto updatedProject = ProjectDto.builder()
                .id(projectId)
                .name("Updated Project")
                .description("Updated Description")
                .build();

        when(projectApiService.updateProject(eq(projectId), any(ProjectUpdateRequest.class)))
                .thenReturn(updatedProject);

        // When & Then
        mockMvc.perform(put("/api/projects/{id}", projectId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Project"));
    }

    @Test
    @WithMockUser(username = "test@bugtracker.com")
    void deleteProject_WithValidId_ReturnsNoContent() throws Exception {
        // Given
        UUID projectId = UUID.randomUUID();

        // When & Then
        mockMvc.perform(delete("/api/projects/{id}", projectId)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(projectApiService).deleteProject(projectId);
    }
}

