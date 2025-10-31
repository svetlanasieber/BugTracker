package com.bugtracker.model.dto;

import com.bugtracker.model.enums.BugPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BugAddDTO {
    
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;
    
    @NotBlank(message = "Description is required")
    @Size(min = 10, message = "Description must be at least 10 characters")
    private String description;
    
    @Size(min = 10, message = "Steps to reproduce must be at least 10 characters")
    private String stepsToReproduce;
    
    @NotNull(message = "Priority is required")
    private BugPriority priority;
    
    @NotNull(message = "Project is required")
    private Long projectId;
    
    private Long assignedToId;
}
