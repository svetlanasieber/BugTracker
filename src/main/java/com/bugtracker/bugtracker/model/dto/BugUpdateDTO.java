package com.bugtracker.bugtracker.model.dto;

import com.bugtracker.bugtracker.model.enums.BugPriority;
import com.bugtracker.bugtracker.model.enums.BugStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for updating bug information.
 * Used to transfer bug update data from the web layer to the service layer.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BugUpdateDTO {

    @NotNull(message = "Bug ID is required")
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 5000, message = "Description must be between 10 and 5000 characters")
    private String description;

    @Size(max = 2000, message = "Steps to reproduce cannot exceed 2000 characters")
    private String stepsToReproduce;

    @NotNull(message = "Status is required")
    private BugStatus status;

    @NotNull(message = "Priority is required")
    private BugPriority priority;

    @NotNull(message = "Project is required")
    private Long projectId;

    private Long assignedToId;
} 