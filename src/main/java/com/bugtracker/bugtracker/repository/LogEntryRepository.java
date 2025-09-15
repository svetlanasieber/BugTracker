package com.bugtracker.bugtracker.repository;

import com.bugtracker.bugtracker.model.entity.LogEntry;
import com.bugtracker.bugtracker.model.enums.LogLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {
    
    List<LogEntry> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(String entityType, Long entityId);
    
    List<LogEntry> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    List<LogEntry> findByLevelOrderByCreatedAtDesc(LogLevel level);
    
    List<LogEntry> findByActionOrderByCreatedAtDesc(String action);
    
    List<LogEntry> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime startDate, LocalDateTime endDate);
    
    List<LogEntry> findTop10ByOrderByCreatedAtDesc();
    

    Page<LogEntry> findByActionAndEntityType(String action, String entityType, Pageable pageable);
    
    Page<LogEntry> findByAction(String action, Pageable pageable);
    
    Page<LogEntry> findByEntityType(String entityType, Pageable pageable);
    
  
    @Query("SELECT DISTINCT l.action FROM LogEntry l ORDER BY l.action")
    List<String> findDistinctActions();
    
    @Query("SELECT DISTINCT l.entityType FROM LogEntry l ORDER BY l.entityType")
    List<String> findDistinctEntityTypes();

    @Query("SELECT l FROM LogEntry l WHERE " +
           "LOWER(l.action) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(l.entityType) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(l.details) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<LogEntry> searchLogs(@Param("query") String query);
} 
