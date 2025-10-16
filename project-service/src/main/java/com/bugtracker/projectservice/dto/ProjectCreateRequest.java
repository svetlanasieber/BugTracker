package com.bugtracker.projectservice.dto;

import com.bugtracker.projectservice.enums.ProjectType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for creating new projects
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCreateRequest {

    @NotBlank(message = "Project name is required")
    @Size(min = 3, max = 100, message = "Project name must be between 3 and 100 characters")
    private String name;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    private String description;

    @NotNull(message = "Project type is required")
    private ProjectType projectType;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Builder.Default
    private boolean isActive = true;

    private Long createdByUserId;
    private String createdByUsername;
    private Set<Long> memberIds;
}

