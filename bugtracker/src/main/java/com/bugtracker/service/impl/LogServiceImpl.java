package com.bugtracker.service.impl;

import com.bugtracker.model.entity.LogEntry;
import com.bugtracker.model.enums.LogLevel;
import com.bugtracker.repository.LogEntryRepository;
import com.bugtracker.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogServiceImpl implements LogService {

    private final LogEntryRepository logEntryRepository;

    @Override
    public LogEntry createLogEntry(String action, String entityType, UUID entityId, Long userId, String details, LogLevel level) {
        LogEntry logEntry = LogEntry.builder()
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .userId(userId)
                .details(details)
                .level(level)
                .createdAt(LocalDateTime.now())
                .build();
        
        LogEntry savedEntry = logEntryRepository.save(logEntry);
        log.debug("Created log entry with ID: {}", savedEntry.getId());
        return savedEntry;
    }
    
    @Override
    public LogEntry createLogEntry(String action, String entityType, UUID entityId, String username, String details) {
        Long userId = null;
        LogLevel level = LogLevel.INFO;
        
        LogEntry logEntry = LogEntry.builder()
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .userId(userId)
                .details(details)
                .level(level)
                .createdAt(LocalDateTime.now())
                .build();
        
        LogEntry savedEntry = logEntryRepository.save(logEntry);
        log.debug("Created log entry with ID: {} (from REST API)", savedEntry.getId());
        return savedEntry;
    }

    @Override
    public List<LogEntry> findLogsByEntityTypeAndId(String entityType, UUID entityId) {
        return logEntryRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(entityType, entityId);
    }

    @Override
    public List<LogEntry> findRecentLogs(int limit) {
        return logEntryRepository.findAll(
                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"))
        ).getContent();
    }
    
    
    
    public List<LogEntry> findLogsByUserId(Long userId) {
        return logEntryRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    public List<LogEntry> findLogsByLevel(LogLevel level) {
        return logEntryRepository.findByLevelOrderByCreatedAtDesc(level);
    }
    
    public List<LogEntry> findLogsByAction(String action) {
        return logEntryRepository.findByActionOrderByCreatedAtDesc(action);
    }
    
    public List<LogEntry> findLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return logEntryRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(startDate, endDate);
    }
    
    
    
    @Override
    public Page<LogEntry> findByActionAndEntityType(String action, String entityType, Pageable pageable) {
        return logEntryRepository.findByActionAndEntityType(action, entityType, pageable);
    }
    
    @Override
    public Page<LogEntry> findByAction(String action, Pageable pageable) {
        return logEntryRepository.findByAction(action, pageable);
    }
    
    @Override
    public Page<LogEntry> findByEntityType(String entityType, Pageable pageable) {
        return logEntryRepository.findByEntityType(entityType, pageable);
    }
    
    @Override
    public Page<LogEntry> findAllLogs(Pageable pageable) {
        return logEntryRepository.findAll(pageable);
    }
    
    @Override
    public LogEntry findById(UUID id) {
        return logEntryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Log entry not found with id: " + id));
    }
    
    @Override
    public List<String> findDistinctActions() {
        return logEntryRepository.findDistinctActions();
    }
    
    @Override
    public List<String> findDistinctEntityTypes() {
        return logEntryRepository.findDistinctEntityTypes();
    }
    
    @Override
    public List<LogEntry> searchLogs(String query) {
        return logEntryRepository.searchLogs(query);
    }
} 