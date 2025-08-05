package com.bugtracker.bugtracker.bug.repository;

import com.bugtracker.bugtracker.bug.model.Bug;
import com.bugtracker.bugtracker.model.enums.BugPriority;
import com.bugtracker.bugtracker.model.enums.BugStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BugRepository extends JpaRepository<Bug, Long> {
    
    List<Bug> findByProject_Id(Long projectId);
    
    List<Bug> findByReporter_Id(Long reporterId);
    
    List<Bug> findByAssignedTo_Id(Long assignedToId);
    

    Optional<Bug> findByTitle(String title);
    
 
    List<Bug> findByStatus(BugStatus status);
    
   
    List<Bug> findByPriority(BugPriority priority);
    
  
    List<Bug> findByUpdatedAtBeforeAndStatusNot(LocalDateTime date, BugStatus status);
    
 
    Long countByAssignedTo_Id(Long userId);
    

    Long countByReporter_Id(Long userId);
    
 
    @Query("SELECT b FROM Bug b WHERE b.assignedTo.id = :userId OR b.reporter.id = :userId ORDER BY b.updatedAt DESC")
    List<Bug> findRecentBugsByUserId(@Param("userId") Long userId, Pageable pageable);
    
  
    default List<Bug> findRecentBugsByUser(Long userId, int limit) {
        return findRecentBugsByUserId(userId, Pageable.ofSize(limit));
    }
    
 
    @Query("SELECT b FROM Bug b WHERE " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Bug> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
} 
