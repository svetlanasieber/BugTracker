package com.bugtracker.bugtracker.web.dto;

import com.bugtracker.bugtracker.model.enums.LogLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for LogEntry.
 * Used for JSON serialization in REST endpoints to prevent LazyInitializationException
 * and provide clean API responses.
 */
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

    /**
     * Static factory method to create DTO from LogEntry entity.
     */
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