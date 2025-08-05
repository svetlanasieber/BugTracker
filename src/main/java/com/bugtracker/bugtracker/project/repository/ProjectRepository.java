package com.bugtracker.bugtracker.project.repository;

import com.bugtracker.bugtracker.project.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    
    @Query("SELECT p FROM Project p JOIN p.members m WHERE m.id = :userId")
    List<Project> findByMemberId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(p) FROM Project p JOIN p.members m WHERE m.id = :userId")
    Long countByMemberId(@Param("userId") Long userId);
    
    Optional<Project> findByName(String name);
    
    List<Project> findByIsActiveTrue();
    
    @Query("SELECT p FROM Project p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Project> searchByKeyword(@Param("keyword") String keyword);
} 