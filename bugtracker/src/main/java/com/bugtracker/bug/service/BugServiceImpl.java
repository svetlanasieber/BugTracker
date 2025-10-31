package com.bugtracker.bug.service;

import com.bugtracker.web.dto.BugAdd;
import com.bugtracker.web.dto.BugUpdate;
import com.bugtracker.bug.model.Bug;
import com.bugtracker.project.model.Project;
import com.bugtracker.user.model.User;
import com.bugtracker.model.enums.BugPriority;
import com.bugtracker.model.enums.BugStatus;
import com.bugtracker.model.enums.LogLevel;
import com.bugtracker.bug.repository.BugRepository;
import com.bugtracker.project.service.ProjectService;
import com.bugtracker.user.repository.UserRepository;
import com.bugtracker.service.AuthService;
import com.bugtracker.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BugServiceImpl implements BugService {

    private final BugRepository bugRepository;
    private final UserRepository userRepository;
    private final ProjectService projectService;
    private final LogService logService;
    private final AuthService authService;

    @Override
    @Transactional
    public Bug createBug(Bug bug) {
        
        bug.setCreatedAt(LocalDateTime.now());
        bug.setUpdatedAt(LocalDateTime.now());
        
        
        if (bug.getStatus() == null) {
            bug.setStatus(BugStatus.NEW);
        }
        
        Bug savedBug = bugRepository.save(bug);
        
        
        logService.createLogEntry(
                "CREATE", 
                "Bug", 
                savedBug.getId(),
                savedBug.getReporter().getId(),
                "Bug created: " + savedBug.getTitle(),
                LogLevel.INFO
        );
        
        log.info("Created new bug with ID: {}", savedBug.getId());
        return savedBug;
    }

    @Override
    public Optional<Bug> findById(Long id) {
        return bugRepository.findById(id);
    }

    @Override
    public List<Bug> findAll() {
        return bugRepository.findAll();
    }

    @Override
    public List<Bug> findByProjectId(Long projectId) {
        return bugRepository.findByProject_Id(projectId);
    }

    @Override
    public List<Bug> findByReporterId(Long reporterId) {
        return bugRepository.findByReporter_Id(reporterId);
    }

    @Override
    public List<Bug> findByAssignedToId(Long assignedToId) {
        return bugRepository.findByAssignedTo_Id(assignedToId);
    }

    @Override
    public List<Bug> findRecentBugsByUser(Long userId, int limit) {
        return bugRepository.findRecentBugsByUser(userId, limit);
    }

    @Override
    @Transactional
    public Bug updateBug(Bug bug) {
        
        Bug existingBug = bugRepository.findById(bug.getId())
                .orElseThrow(() -> new RuntimeException("Bug not found with id: " + bug.getId()));
        
        
        bug.setUpdatedAt(LocalDateTime.now());
        
        
        if (BugStatus.CLOSED.equals(bug.getStatus()) && !BugStatus.CLOSED.equals(existingBug.getStatus())) {
            bug.setClosedAt(LocalDateTime.now());
        }
        
        Bug updatedBug = bugRepository.save(bug);
        
        
        logService.createLogEntry(
                "UPDATE",
                "Bug",
                updatedBug.getId(),
                null, 
                "Bug updated: " + updatedBug.getTitle(),
                LogLevel.INFO
        );
        
        log.info("Updated bug with ID: {}", updatedBug.getId());
        return updatedBug;
    }

    @Override
    @Transactional
    public void deleteBug(Long id) {
        Bug bug = bugRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bug not found with id: " + id));
        
        bugRepository.delete(bug);
        
        
        logService.createLogEntry(
                "DELETE",
                "Bug",
                id,
                null, 
                "Bug deleted: " + bug.getTitle(),
                LogLevel.WARNING
        );
        
        log.info("Deleted bug with ID: {}", id);
    }

    @Override
    public long countAssignedBugs(Long userId) {
        return bugRepository.countByAssignedTo_Id(userId);
    }

    @Override
    public long countReportedBugs(Long userId) {
        return bugRepository.countByReporter_Id(userId);
    }
    
    
    
    @Override
    public Page<Bug> searchBugs(String keyword, Pageable pageable) {
        return bugRepository.searchByKeyword(keyword, pageable);
    }
    
    @Override
    @Transactional
    public void assignBug(Long bugId, Long userId) {
        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new RuntimeException("Bug not found with id: " + bugId));
        
        if (userId == null) {
            bug.setAssignedTo(null);
            log.info("Unassigned bug with ID: {}", bugId);
        } else {
            userRepository.findById(userId).ifPresent(user -> {
                bug.setAssignedTo(user);
                log.info("Assigned bug with ID: {} to user: {}", bugId, userId);
            });
        }
        
        bug.setUpdatedAt(LocalDateTime.now());
        bugRepository.save(bug);
        
        
        logService.createLogEntry(
                "ASSIGN",
                "Bug",
                bugId,
                null,
                userId == null ? "Bug unassigned" : "Bug assigned to user ID: " + userId,
                LogLevel.INFO
        );
    }
    
    @Override
    @Transactional
    public void changeBugStatus(Long bugId, BugStatus newStatus) {
        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new RuntimeException("Bug not found with id: " + bugId));
        
        BugStatus oldStatus = bug.getStatus();
        bug.setStatus(newStatus);
        bug.setUpdatedAt(LocalDateTime.now());
        
        
        if (BugStatus.CLOSED.equals(newStatus) && !BugStatus.CLOSED.equals(oldStatus)) {
            bug.setClosedAt(LocalDateTime.now());
        }
        
        bugRepository.save(bug);
        
        
        logService.createLogEntry(
                "STATUS_CHANGE",
                "Bug",
                bugId,
                null,
                "Bug status changed from " + oldStatus + " to " + newStatus,
                LogLevel.INFO
        );
        
        log.info("Changed bug status from {} to {} for bug ID: {}", oldStatus, newStatus, bugId);
    }

    @Override
    public long countBugsByStatus(BugStatus status, Long userId) {
        
        return bugRepository.findAll().stream()
                .filter(bug -> bug.getStatus() == status)
                .filter(bug -> bug.getProject().getMembers().stream()
                      .anyMatch(member -> member.getId().equals(userId)))
                .count();
    }

    @Override
    public long countBugsByPriority(BugPriority priority, Long userId) {
        
        return bugRepository.findAll().stream()
                .filter(bug -> bug.getPriority() == priority)
                .filter(bug -> bug.getProject().getMembers().stream()
                      .anyMatch(member -> member.getId().equals(userId)))
                .count();
    }
    
    
    @Override
    public List<Bug> findBugsNotUpdatedSince(LocalDateTime date) {
        return bugRepository.findAll().stream()
                .filter(bug -> bug.getUpdatedAt() != null && bug.getUpdatedAt().isBefore(date))
                .toList();
    }
    
    @Override
    public List<Bug> findBugsWithFilters(Long projectId, BugStatus status, BugPriority priority) {
        List<Bug> bugs;
        
        if (projectId != null) {
            
            bugs = findByProjectId(projectId);
        } else {
            
            bugs = findAll();
        }
        
        
        if (status != null || priority != null) {
            bugs = bugs.stream()
                .filter(bug -> status == null || bug.getStatus() == status)
                .filter(bug -> priority == null || bug.getPriority() == priority)
                .toList();
        }
        
        return bugs;
    }
    
    @Override
    @Transactional
    public Bug createBugFromDTO(BugAdd bugAdd, String reporterUsername) {
        
        User reporter = userRepository.findByEmail(reporterUsername)
                .orElseThrow(() -> new RuntimeException("Reporter not found"));
        
        
        Project project = projectService.findById(bugAdd.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        
        User assignedTo = null;
        if (bugAdd.getAssignedToId() != null) {
            assignedTo = userRepository.findById(bugAdd.getAssignedToId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }
        
        
        Bug bug = Bug.builder()
                .title(bugAdd.getTitle())
                .description(bugAdd.getDescription())
                .stepsToReproduce(bugAdd.getStepsToReproduce())
                .status(BugStatus.NEW)
                .priority(bugAdd.getPriority())
                .project(project)
                .reporter(reporter)
                .assignedTo(assignedTo)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        return createBug(bug);
    }
    
    @Override
    @Transactional
    public Bug prepareUpdateBug(Long id, Bug updatedBug) {
        
        Bug existingBug = findById(id)
                .orElseThrow(() -> new RuntimeException("Bug not found with id: " + id));
        
        
        updatedBug.setId(id);
        updatedBug.setReporter(existingBug.getReporter());
        updatedBug.setCreatedAt(existingBug.getCreatedAt());
        updatedBug.setUpdatedAt(LocalDateTime.now());
        
        return updateBug(updatedBug);
    }

    @Override
    @Transactional
    public String assignBugWithMessage(Long bugId, Long userId) {
        assignBug(bugId, userId);
        return userId == null ? "Bug unassigned successfully!" : "Bug assigned successfully!";
    }
    
    @Override
    @Transactional
    public Bug updateBugFromDTO(BugUpdate bugUpdate) {
        
        Bug existingBug = findById(bugUpdate.getId())
                .orElseThrow(() -> new RuntimeException("Bug not found with id: " + bugUpdate.getId()));
        
        
        Project project = projectService.findById(bugUpdate.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        
        User assignedTo = null;
        if (bugUpdate.getAssignedToId() != null) {
            assignedTo = userRepository.findById(bugUpdate.getAssignedToId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }
        
        
        Bug updatedBug = Bug.builder()
                .id(existingBug.getId())
                .title(bugUpdate.getTitle())
                .description(bugUpdate.getDescription())
                .stepsToReproduce(bugUpdate.getStepsToReproduce())
                .status(bugUpdate.getStatus())
                .priority(bugUpdate.getPriority())
                .project(project)
                .assignedTo(assignedTo)
                
                .reporter(existingBug.getReporter())
                .createdAt(existingBug.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .closedAt(existingBug.getClosedAt())
                .comments(existingBug.getComments())
                .build();
        
        
        if (BugStatus.CLOSED.equals(bugUpdate.getStatus()) && 
            !BugStatus.CLOSED.equals(existingBug.getStatus())) {
            updatedBug.setClosedAt(LocalDateTime.now());
        }
        
        return updateBug(updatedBug);
    }
    
    @Override
    @Transactional
    public Bug createBugFromDTOWithCurrentUser(BugAdd bugAdd) {
        String currentUsername = authService.getCurrentUsername();
        return createBugFromDTO(bugAdd, currentUsername);
    }
} 