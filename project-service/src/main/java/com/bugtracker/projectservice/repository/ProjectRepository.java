package com.bugtracker.projectservice.repository;

import com.bugtracker.projectservice.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    /**
     * Find projects by creator user ID
     */
    List<Project> findByCreatedByUserId(Long userId);

    /**
     * Find projects by creator username
     */
    List<Project> findByCreatedByUsername(String username);

    /**
     * Find active projects only
     */
    List<Project> findByIsActiveTrue();

    /**
     * Find projects where user is a member (by checking memberUserIds string)
     */
    @Query("SELECT p FROM Project p WHERE p.memberUserIds LIKE CONCAT('%', :userId, '%')")
    List<Project> findProjectsForUser(@Param("userId") Long userId);

    /**
     * Find projects by name containing (case insensitive)
     */
    List<Project> findByNameContainingIgnoreCase(String name);

    /**
     * Count projects created by user
     */
    long countByCreatedByUserId(Long userId);
}

