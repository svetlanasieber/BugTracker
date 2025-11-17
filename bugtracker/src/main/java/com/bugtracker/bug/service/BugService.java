package com.bugtracker.bug.service;

import com.bugtracker.web.dto.BugAdd;
import com.bugtracker.web.dto.BugUpdate;
import com.bugtracker.bug.model.Bug;
import com.bugtracker.model.enums.BugPriority;
import com.bugtracker.model.enums.BugStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BugService {
    Bug createBug(Bug bug);
    Optional<Bug> findById(UUID id);
    
    List<Bug> findAll();
    
    List<Bug> findByProjectId(UUID projectId);
    
    List<Bug> findByReporterId(Long reporterId);
    
    List<Bug> findByAssignedToId(Long assignedToId);
    
    List<Bug> findRecentBugsByUser(Long userId, int limit);
    
    Bug updateBug(Bug bug);
    void deleteBug(UUID id);
    long countAssignedBugs(Long userId);
    long countReportedBugs(Long userId);
    void assignBug(UUID bugId, Long userId);
    void changeBugStatus(UUID bugId, BugStatus status);
    
    Page<Bug> searchBugs(String keyword, Pageable pageable);
    long countBugsByStatus(BugStatus status, Long userId);
    long countBugsByPriority(BugPriority priority, Long userId);
    
    List<Bug> findBugsNotUpdatedSince(LocalDateTime date);
    
    List<Bug> findBugsWithFilters(UUID projectId, BugStatus status, BugPriority priority);
    
    Bug createBugFromDTO(BugAdd bugAdd, String reporterUsername);
    Bug prepareUpdateBug(UUID id, Bug updatedBug);
    String assignBugWithMessage(UUID bugId, Long userId);
    Bug updateBugFromDTO(BugUpdate bugUpdate);
    Bug createBugFromDTOWithCurrentUser(BugAdd bugAdd);

} 
