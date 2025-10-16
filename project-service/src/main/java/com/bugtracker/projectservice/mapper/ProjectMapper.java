package com.bugtracker.projectservice.mapper;

import com.bugtracker.projectservice.dto.ProjectDto;
import com.bugtracker.projectservice.model.Project;
import org.springframework.stereotype.Component;


@Component
public class ProjectMapper {

    public ProjectDto toDto(Project project) { 
        if (project == null) {
            return null;
        }

        return ProjectDto.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .projectType(project.getProjectType())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .isActive(project.isActive())
                .createdByUserId(project.getCreatedByUserId())
                .createdByUsername(project.getCreatedByUsername())
                .memberIds(project.getMemberIds())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }

    public Project toEntity(ProjectDto dto) {
        if (dto == null) {
            return null;
        }

        Project project = Project.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .projectType(dto.getProjectType())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .isActive(dto.isActive())
                .createdByUserId(dto.getCreatedByUserId())
                .createdByUsername(dto.getCreatedByUsername())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();

       
        if (dto.getMemberIds() != null) {
            project.setMemberIds(dto.getMemberIds());
        }

        return project;
    }
}

