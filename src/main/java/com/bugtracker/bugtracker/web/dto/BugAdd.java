package com.bugtracker.bugtracker.web.dto;

import com.bugtracker.bugtracker.model.enums.BugPriority;
import com.bugtracker.bugtracker.validation.groups.OnCreate;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BugAdd {
    
    @NotBlank(message = "Title is required", groups = OnCreate.class)
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters", groups = OnCreate.class)
    @Pattern(regexp = "^[^<>\"]*$", message = "Title cannot contain HTML tags or quotes", groups = OnCreate.class)
    private String title;
    
    @NotBlank(message = "Description is required", groups = OnCreate.class)
    @Size(min = 10, max = 5000, message = "Description must be between 10 and 5000 characters", groups = OnCreate.class)
    private String description;
    
    @Size(min = 10, max = 2000, message = "Steps to reproduce must be between 10 and 2000 characters if provided", groups = OnCreate.class)
    private String stepsToReproduce;
    
    @NotNull(message = "Priority is required", groups = OnCreate.class)
    private BugPriority priority;
    
    @NotNull(message = "Project is required", groups = OnCreate.class)
    @Positive(message = "Project ID must be positive", groups = OnCreate.class)
    private Long projectId;
    
    @Positive(message = "Assigned user ID must be positive", groups = OnCreate.class)
    private Long assignedToId;
} 