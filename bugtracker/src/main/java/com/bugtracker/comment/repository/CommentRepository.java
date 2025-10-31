package com.bugtracker.comment.repository;

import com.bugtracker.comment.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    List<Comment> findByBugId(UUID bugId);
    
    Page<Comment> findByBugId(UUID bugId, Pageable pageable);
    
    List<Comment> findByAuthorId(Long authorId);
    
    void deleteAllByBugId(UUID bugId);
} 