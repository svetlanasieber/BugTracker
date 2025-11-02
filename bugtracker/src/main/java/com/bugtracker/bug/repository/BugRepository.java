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

@Repository
public interface BugRepository extends JpaRepository<Bug, UUID> {
    
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
    
    Long countByAssignedTo_Id(Long userId);
    Long countByReporter_Id(Long userId);
    
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
    
    @Query("SELECT b FROM Bug b " +
           "LEFT JOIN FETCH b.reporter " +
           "LEFT JOIN FETCH b.assignedTo " +
           "LEFT JOIN FETCH b.project " +
           "WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Bug> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    @EntityGraph(attributePaths = {"reporter", "assignedTo", "project"})
    @Query("SELECT b FROM Bug b")
    List<Bug> findAllWithAssociations();
    
    @EntityGraph(attributePaths = {"reporter", "assignedTo", "project"})
    @Query("SELECT b FROM Bug b WHERE b.id = :id")
    Optional<Bug> findByIdWithAssociations(@Param("id") UUID id);
} 