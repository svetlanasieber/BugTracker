package com.bugtracker.bugtracker.web.dto;

import com.bugtracker.bugtracker.model.enums.LogLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogEntryDTO {

    private Long id;
    private String action;
    private String entityType;
    private Long entityId;
    private Long userId;
    private String details;
    private LogLevel level;
    private LocalDateTime createdAt;

    public static LogEntryDTO fromEntity(com.bugtracker.bugtracker.model.entity.LogEntry logEntry) {
        return LogEntryDTO.builder()
                .id(logEntry.getId())
                .action(logEntry.getAction())
                .entityType(logEntry.getEntityType())
                .entityId(logEntry.getEntityId())
                .userId(logEntry.getUserId())
                .details(logEntry.getDetails())
                .level(logEntry.getLevel())
                .createdAt(logEntry.getCreatedAt())
                .build();
    }
} 
