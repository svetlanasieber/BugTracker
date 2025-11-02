package com.bugtracker.web.dto;

import com.bugtracker.model.enums.LogLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogEntryDTO {

    private UUID id;
    private String action;
    private String entityType;
    private UUID entityId;
    private Long userId;
    private String details;
    private LogLevel level;
    private LocalDateTime createdAt;

    public static LogEntryDTO fromEntity(com.bugtracker.model.entity.LogEntry logEntry) {
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