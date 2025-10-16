package com.bugtracker.projectservice.repository;

import com.bugtracker.projectservice.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByCreatedByUserId(Long userId);

    List<Project> findByCreatedByUsername(String username);

 
    List<Project> findByIsActiveTrue();

  
    @Query("SELECT p FROM Project p WHERE p.memberUserIds LIKE CONCAT('%', :userId, '%')")
    List<Project> findProjectsForUser(@Param("userId") Long userId);


    List<Project> findByNameContainingIgnoreCase(String name);


    long countByCreatedByUserId(Long userId);
}

