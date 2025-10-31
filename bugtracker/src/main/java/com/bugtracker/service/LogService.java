package com.bugtracker.service;

import com.bugtracker.model.entity.LogEntry;
import com.bugtracker.model.enums.LogLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface LogService {
    LogEntry createLogEntry(String action, String entityType, UUID entityId, Long userId, String details, LogLevel level);
    LogEntry createLogEntry(String action, String entityType, UUID entityId, String username, String details);
    List<LogEntry> findLogsByEntityTypeAndId(String entityType, UUID entityId);
    List<LogEntry> findRecentLogs(int limit);
    Page<LogEntry> findByActionAndEntityType(String action, String entityType, Pageable pageable);
    Page<LogEntry> findByAction(String action, Pageable pageable);
    Page<LogEntry> findByEntityType(String entityType, Pageable pageable);
    Page<LogEntry> findAllLogs(Pageable pageable);
    LogEntry findById(UUID id);
    List<String> findDistinctActions();
    List<String> findDistinctEntityTypes();
    List<LogEntry> searchLogs(String query);
} 