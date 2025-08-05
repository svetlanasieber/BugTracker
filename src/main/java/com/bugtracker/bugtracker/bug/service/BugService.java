package com.bugtracker.bugtracker.bug.service;

import com.bugtracker.bugtracker.web.dto.BugAdd;
import com.bugtracker.bugtracker.web.dto.BugUpdate;
import com.bugtracker.bugtracker.bug.model.Bug;
import com.bugtracker.bugtracker.model.enums.BugPriority;
import com.bugtracker.bugtracker.model.enums.BugStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing Bug entities in the Bug Tracker application.
 * 
 * <p>This service provides comprehensive business logic operations for bug management including:
 * <ul>
 *   <li>CRUD operations (Create, Read, Update, Delete)</li>
 *   <li>Search and filtering capabilities</li>
 *   <li>Assignment and status management</li>
 *   <li>DTO-based operations for web layer integration</li>
 *   <li>Business rule enforcement and validation</li>
 * </ul>
 * </p>
 * 
 * <p>All operations are transactional where appropriate and include proper error handling
 * through the application's global exception handling mechanism.</p>
 * 
 * @author Bug Tracker Team
 * @version 1.0
 * @since 1.0
 */
public interface BugService {
    /**
     * Creates a new bug in the system.
     * 
     * @param bug the bug entity to create, must not be null
     * @return the persisted bug entity with generated ID and timestamps
     * @throws IllegalArgumentException if bug is null or has invalid data
     */
    Bug createBug(Bug bug);
    
    /**
     * Retrieves a bug by its unique identifier.
     * 
     * @param id the unique identifier of the bug, must not be null
     * @return an Optional containing the bug if found, empty otherwise
     * @throws IllegalArgumentException if id is null or negative
     */
    Optional<Bug> findById(Long id);
    
    /**
     * Retrieves all bugs in the system.
     * 
     * @return a list of all bugs, empty list if no bugs exist
     */
    List<Bug> findAll();
    
    /**
     * Finds all bugs associated with a specific project.
     * 
     * @param projectId the unique identifier of the project, must not be null
     * @return a list of bugs belonging to the project, empty if none found
     * @throws IllegalArgumentException if projectId is null or negative
     */
    List<Bug> findByProjectId(Long projectId);
    
    /**
     * Finds all bugs reported by a specific user.
     * 
     * @param reporterId the unique identifier of the reporter, must not be null
     * @return a list of bugs reported by the user, empty if none found
     * @throws IllegalArgumentException if reporterId is null or negative
     */
    List<Bug> findByReporterId(Long reporterId);
    
    /**
     * Finds all bugs assigned to a specific user.
     * 
     * @param assignedToId the unique identifier of the assignee, must not be null
     * @return a list of bugs assigned to the user, empty if none found
     * @throws IllegalArgumentException if assignedToId is null or negative
     */
    List<Bug> findByAssignedToId(Long assignedToId);
    
    /**
     * Retrieves the most recently updated bugs for a user.
     * 
     * @param userId the unique identifier of the user, must not be null
     * @param limit the maximum number of bugs to return, must be positive
     * @return a list of recent bugs, limited by the specified count
     * @throws IllegalArgumentException if userId is null/negative or limit is not positive
     */
    List<Bug> findRecentBugsByUser(Long userId, int limit);
    
    /**
     * Updates an existing bug with new information.
     * 
     * @param bug the bug entity with updated information, must not be null and must have valid ID
     * @return the updated bug entity
     * @throws IllegalArgumentException if bug is null or has invalid data
     * @throws EntityNotFoundException if bug with the given ID does not exist
     */
    Bug updateBug(Bug bug);
    
    /**
     * Permanently deletes a bug from the system.
     * 
     * @param id the unique identifier of the bug to delete, must not be null
     * @throws IllegalArgumentException if id is null or negative
     * @throws EntityNotFoundException if bug with the given ID does not exist
     */
    void deleteBug(Long id);
    
    /**
     * Counts the number of bugs assigned to a specific user.
     * 
     * @param userId the unique identifier of the user, must not be null
     * @return the count of assigned bugs
     * @throws IllegalArgumentException if userId is null or negative
     */
    long countAssignedBugs(Long userId);
    
    /**
     * Counts the number of bugs reported by a specific user.
     * 
     * @param userId the unique identifier of the user, must not be null
     * @return the count of reported bugs
     * @throws IllegalArgumentException if userId is null or negative
     */
    long countReportedBugs(Long userId);
    
    // ========== Bug Assignment and Status Management ==========
    
    /**
     * Assigns a bug to a specific user.
     * 
     * @param bugId the unique identifier of the bug, must not be null
     * @param userId the unique identifier of the user to assign, must not be null
     * @throws IllegalArgumentException if bugId or userId is null or negative
     * @throws EntityNotFoundException if bug or user with given IDs does not exist
     */
    void assignBug(Long bugId, Long userId);
    
    /**
     * Changes the status of a specific bug.
     * 
     * @param bugId the unique identifier of the bug, must not be null
     * @param status the new status to set, must not be null
     * @throws IllegalArgumentException if bugId is null/negative or status is null
     * @throws EntityNotFoundException if bug with the given ID does not exist
     */
    void changeBugStatus(Long bugId, BugStatus status);
    
    /**
     * Searches for bugs using a keyword with pagination support.
     * 
     * @param keyword the search term to look for in bug title and description, can be null/empty
     * @param pageable the pagination parameters, must not be null
     * @return a page of bugs matching the search criteria
     * @throws IllegalArgumentException if pageable is null
     */
    Page<Bug> searchBugs(String keyword, Pageable pageable);
    
    // ========== Statistical Operations ==========
    
    /**
     * Counts bugs by status for a specific user (as reporter or assignee).
     * 
     * @param status the bug status to count, must not be null
     * @param userId the unique identifier of the user, must not be null
     * @return the count of bugs with the specified status for the user
     * @throws IllegalArgumentException if status or userId is null, or userId is negative
     */
    long countBugsByStatus(BugStatus status, Long userId);
    
    /**
     * Counts bugs by priority for a specific user (as reporter or assignee).
     * 
     * @param priority the bug priority to count, must not be null
     * @param userId the unique identifier of the user, must not be null
     * @return the count of bugs with the specified priority for the user
     * @throws IllegalArgumentException if priority or userId is null, or userId is negative
     */
    long countBugsByPriority(BugPriority priority, Long userId);
    
    // ========== Maintenance and Scheduling Operations ==========
    
    /**
     * Finds bugs that have not been updated since a specific date.
     * Used primarily by the auto-close scheduler for maintenance operations.
     * 
     * @param date the cutoff date for finding stale bugs, must not be null
     * @return a list of bugs not updated since the specified date
     * @throws IllegalArgumentException if date is null or in the future
     */
    List<Bug> findBugsNotUpdatedSince(LocalDateTime date);
    
    // ========== Business Logic Operations ==========
    
    /**
     * Finds bugs with applied filters for project, status, and priority.
     * 
     * @param projectId the project ID to filter by, can be null for all projects
     * @param status the status to filter by, can be null for all statuses
     * @param priority the priority to filter by, can be null for all priorities
     * @return a list of bugs matching the specified filters
     */
    List<Bug> findBugsWithFilters(Long projectId, BugStatus status, BugPriority priority);
    
    /**
     * Creates a bug from DTO with specified reporter username.
     * 
     * @param bugAdd the DTO containing bug creation data, must not be null
     * @param reporterUsername the username of the reporter, must not be null or empty
     * @return the created bug entity
     * @throws IllegalArgumentException if bugAdd is null or reporterUsername is null/empty
     * @throws EntityNotFoundException if reporter user is not found
     */
    Bug createBugFromDTO(BugAdd bugAdd, String reporterUsername);
    
    /**
     * Prepares a bug for update by merging existing data with changes.
     * 
     * @param id the unique identifier of the bug to update, must not be null
     * @param updatedBug the bug entity with updated data, must not be null
     * @return the prepared bug entity ready for persistence
     * @throws IllegalArgumentException if id is null/negative or updatedBug is null
     * @throws EntityNotFoundException if bug with the given ID does not exist
     */
    Bug prepareUpdateBug(Long id, Bug updatedBug);
    
    // ========== Enhanced Business Operations (Controller Integration) ==========
    
    /**
     * Assigns or unassigns a bug to/from a user and returns an appropriate success message.
     * This method provides user-friendly feedback for web interface operations.
     * 
     * <p>If userId is null, the bug will be unassigned from its current assignee.
     * Otherwise, the bug will be assigned to the specified user.</p>
     * 
     * @param bugId the unique identifier of the bug to assign, must not be null
     * @param userId the unique identifier of the user to assign, null to unassign
     * @return a localized success message describing the operation performed
     * @throws IllegalArgumentException if bugId is null or negative
     * @throws EntityNotFoundException if bug (or user when assigning) is not found
     */
    String assignBugWithMessage(Long bugId, Long userId);
    
    /**
     * Updates a bug from a BugUpdate DTO with comprehensive validation and business logic.
     * This method handles the complete update workflow including data validation,
     * business rule enforcement, and entity mapping.
     * 
     * @param bugUpdate the DTO containing update information, must not be null with valid ID
     * @return the updated and persisted bug entity
     * @throws IllegalArgumentException if bugUpdate is null or contains invalid data
     * @throws EntityNotFoundException if bug with the specified ID does not exist
     * @throws ValidationException if business rules are violated
     */
    Bug updateBugFromDTO(BugUpdate bugUpdate);
    
    /**
     * Creates a bug from a BugAdd DTO using the current authenticated user as the reporter.
     * This method automatically resolves the current user context and sets them as the bug reporter.
     * 
     * <p>The method handles:</p>
     * <ul>
     *   <li>Automatic reporter assignment from security context</li>
     *   <li>DTO validation and mapping to entity</li>
     *   <li>Default value assignment (timestamps, initial status)</li>
     *   <li>Business rule validation</li>
     * </ul>
     * 
     * @param bugAdd the DTO containing bug creation data, must not be null
     * @return the created and persisted bug entity with assigned reporter
     * @throws IllegalArgumentException if bugAdd is null or contains invalid data
     * @throws SecurityException if no authenticated user is found
     * @throws ValidationException if business rules are violated
     */
    Bug createBugFromDTOWithCurrentUser(BugAdd bugAdd);
} 