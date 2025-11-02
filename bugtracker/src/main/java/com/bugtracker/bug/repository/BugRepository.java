package com.bugtracker.bug.repository;

import com.bugtracker.bug.model.Bug;
import com.bugtracker.model.enums.BugPriority;
import com.bugtracker.model.enums.BugStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Bug Repository with N+1 Query Optimization
 * 
 * Uses @EntityGraph to eagerly fetch associations and prevent N+1 queries
 */
@Repository
public interface BugRepository extends JpaRepository<Bug, UUID> {
    
    // Optimized queries with @EntityGraph to fetch reporter and assignedTo eagerly
    @EntityGraph(attributePaths = {"reporter", "assignedTo", "project"})
    List<Bug> findByProject_Id(UUID projectId);
    
    @EntityGraph(attributePaths = {"reporter", "assignedTo", "project"})
    List<Bug> findByReporter_Id(Long reporterId);
    
    @EntityGraph(attributePaths = {"reporter", "assignedTo", "project"})
    List<Bug> findByAssignedTo_Id(Long assignedToId);
    
    @EntityGraph(attributePaths = {"reporter", "assignedTo", "project"})
    Optional<Bug> findByTitle(String title);
    
    @EntityGraph(attributePaths = {"reporter", "assignedTo", "project"})
    List<Bug> findByStatus(BugStatus status);
    
    @EntityGraph(attributePaths = {"reporter", "assignedTo", "project"})
    List<Bug> findByPriority(BugPriority priority);
    
    @EntityGraph(attributePaths = {"reporter", "assignedTo", "project"})
    List<Bug> findByUpdatedAtBeforeAndStatusNot(LocalDateTime date, BugStatus status);
    
    // Count queries don't need EntityGraph (they don't fetch entities)
    Long countByAssignedTo_Id(Long userId);
    Long countByReporter_Id(Long userId);
    
    // Recent bugs with optimized fetching
    @Query("SELECT b FROM Bug b " +
           "LEFT JOIN FETCH b.reporter " +
           "LEFT JOIN FETCH b.assignedTo " +
           "LEFT JOIN FETCH b.project " +
           "WHERE b.assignedTo.id = :userId OR b.reporter.id = :userId " +
           "ORDER BY b.updatedAt DESC")
    List<Bug> findRecentBugsByUserId(@Param("userId") Long userId, Pageable pageable);
    
    default List<Bug> findRecentBugsByUser(Long userId, int limit) {
        return findRecentBugsByUserId(userId, Pageable.ofSize(limit));
    }
    
    // Search with optimized fetching
    @Query("SELECT b FROM Bug b " +
           "LEFT JOIN FETCH b.reporter " +
           "LEFT JOIN FETCH b.assignedTo " +
           "LEFT JOIN FETCH b.project " +
           "WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Bug> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    // Optimized findAll for listing pages
    @EntityGraph(attributePaths = {"reporter", "assignedTo", "project"})
    @Query("SELECT b FROM Bug b")
    List<Bug> findAllWithAssociations();
    
    // Find by ID with all associations (for details page)
    @EntityGraph(attributePaths = {"reporter", "assignedTo", "project"})
    @Query("SELECT b FROM Bug b WHERE b.id = :id")
    Optional<Bug> findByIdWithAssociations(@Param("id") UUID id);
} 