package com.bugtracker.bugtracker.web.dto;

import com.bugtracker.bugtracker.model.enums.ProjectType;
import com.bugtracker.bugtracker.validation.groups.OnCreate;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectAdd {

    @NotBlank(message = "Project name is required", groups = OnCreate.class)
    @Size(min = 3, max = 100, message = "Project name must be between 3 and 100 characters", groups = OnCreate.class)
    @Pattern(regexp = "^[^<>\"]*$", message = "Project name cannot contain HTML tags or quotes", groups = OnCreate.class)
    private String name;

    @NotBlank(message = "Description is required", groups = OnCreate.class)
    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters", groups = OnCreate.class)
    private String description;

    @NotNull(message = "Project type is required", groups = OnCreate.class)
    private ProjectType projectType;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Builder.Default
    private boolean isActive = true;

  
    private List<Long> memberIds;
} 
