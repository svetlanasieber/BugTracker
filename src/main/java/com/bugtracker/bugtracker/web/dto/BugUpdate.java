package com.bugtracker.bugtracker.web.dto;

import com.bugtracker.bugtracker.model.enums.BugPriority;
import com.bugtracker.bugtracker.model.enums.BugStatus;
import com.bugtracker.bugtracker.validation.ValidBugStatus;
import com.bugtracker.bugtracker.validation.groups.OnUpdate;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BugUpdate {

    @NotNull(message = "Bug ID is required", groups = OnUpdate.class)
    @Positive(message = "Bug ID must be positive", groups = OnUpdate.class)
    private Long id;

    @NotBlank(message = "Title is required", groups = OnUpdate.class)
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters", groups = OnUpdate.class)
    @Pattern(regexp = "^[^<>\"]*$", message = "Title cannot contain HTML tags or quotes", groups = OnUpdate.class)
    private String title;

    @NotBlank(message = "Description is required", groups = OnUpdate.class)
    @Size(min = 10, max = 5000, message = "Description must be between 10 and 5000 characters", groups = OnUpdate.class)
    private String description;

    @Size(max = 2000, message = "Steps to reproduce cannot exceed 2000 characters", groups = OnUpdate.class)
    private String stepsToReproduce;

    @NotNull(message = "Status is required", groups = OnUpdate.class)
    private BugStatus status;

    @NotNull(message = "Priority is required", groups = OnUpdate.class)
    private BugPriority priority;

    @NotNull(message = "Project is required", groups = OnUpdate.class)
    @Positive(message = "Project ID must be positive", groups = OnUpdate.class)
    private Long projectId;

    @Positive(message = "Assigned user ID must be positive", groups = OnUpdate.class)
    private Long assignedToId;
} 
