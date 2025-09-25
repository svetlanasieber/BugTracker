package com.bugtracker.bugtracker.comment.repository;

import com.bugtracker.bugtracker.comment.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    List<Comment> findByBugId(Long bugId);
    
    Page<Comment> findByBugId(Long bugId, Pageable pageable);
    
    List<Comment> findByAuthorId(Long authorId);
    
    void deleteAllByBugId(Long bugId);
} 