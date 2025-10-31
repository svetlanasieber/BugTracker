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

public interface BugService {
    
    Bug createBug(Bug bug);
    
    
    Optional<Bug> findById(Long id);
    
    
    List<Bug> findAll();
    
    
    List<Bug> findByProjectId(Long projectId);
    
    
    List<Bug> findByReporterId(Long reporterId);
    
    
    List<Bug> findByAssignedToId(Long assignedToId);
    
    
    List<Bug> findRecentBugsByUser(Long userId, int limit);
    
    
    Bug updateBug(Bug bug);
    
    
    void deleteBug(Long id);
    
    
    long countAssignedBugs(Long userId);
    
    
    long countReportedBugs(Long userId);
    
    
    
    
    void assignBug(Long bugId, Long userId);
    
    
    void changeBugStatus(Long bugId, BugStatus status);
    
    
    Page<Bug> searchBugs(String keyword, Pageable pageable);
    
    
    
    
    long countBugsByStatus(BugStatus status, Long userId);
    
    
    long countBugsByPriority(BugPriority priority, Long userId);
    
    
    
    
    List<Bug> findBugsNotUpdatedSince(LocalDateTime date);
    
    
    
    
    List<Bug> findBugsWithFilters(Long projectId, BugStatus status, BugPriority priority);
    
    
    Bug createBugFromDTO(BugAdd bugAdd, String reporterUsername);
    
    
    Bug prepareUpdateBug(Long id, Bug updatedBug);
    
    
    
    
    String assignBugWithMessage(Long bugId, Long userId);
    
    
    Bug updateBugFromDTO(BugUpdate bugUpdate);
    
    
    Bug createBugFromDTOWithCurrentUser(BugAdd bugAdd);
} 