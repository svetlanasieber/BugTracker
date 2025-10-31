package com.bugtracker.client.dto;

import com.bugtracker.model.enums.ProjectType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectUpdateRequest {
    private Long id;
    private String name;
    private String description;
    private ProjectType projectType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean isActive;
    private Set<Long> memberIds;
}

