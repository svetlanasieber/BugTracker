package com.bugtracker.bugtracker.web.dto;

import com.bugtracker.bugtracker.model.enums.ProjectType;
import com.bugtracker.bugtracker.validation.groups.OnUpdate;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object for updating existing projects.
 * Used to transfer project update data from the web layer to the service layer.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectUpdate {

    @NotNull(message = "Project ID is required", groups = OnUpdate.class)
    @Positive(message = "Project ID must be positive", groups = OnUpdate.class)
    private Long id;

    @NotBlank(message = "Project name is required", groups = OnUpdate.class)
    @Size(min = 3, max = 100, message = "Project name must be between 3 and 100 characters", groups = OnUpdate.class)
    @Pattern(regexp = "^[^<>\"]*$", message = "Project name cannot contain HTML tags or quotes", groups = OnUpdate.class)
    private String name;

    @NotBlank(message = "Description is required", groups = OnUpdate.class)
    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters", groups = OnUpdate.class)
    private String description;

    @NotNull(message = "Project type is required", groups = OnUpdate.class)
    private ProjectType projectType;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private boolean isActive;

    // List of user IDs to assign to the project
    private List<Long> memberIds;
} 