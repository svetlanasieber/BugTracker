package com.bugtracker.projectservice.dto;

import com.bugtracker.projectservice.enums.ProjectType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {
    private UUID id;
    private String name;
    private String description;
    private ProjectType projectType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean isActive;
    private Long createdByUserId;
    private String createdByUsername;
    private Set<Long> memberIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
