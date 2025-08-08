package com.bugtracker.bugtracker.comment.service;

import com.bugtracker.bugtracker.comment.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentService {
    
    Comment createComment(Long bugId, Long authorId, String content);
    
    Comment getComment(Long commentId);
    
    List<Comment> getCommentsByBugId(Long bugId);
    
    Page<Comment> getCommentsByBugId(Long bugId, Pageable pageable);
    
    Comment updateComment(Long commentId, String content);
    
    void deleteComment(Long commentId);
    
    void deleteAllCommentsForBug(Long bugId);
    

    Comment createCommentWithCurrentUser(Long bugId, String content, String username);
    

    Comment updateCommentWithAuthorization(Long commentId, String content, String username);
    
 
    void deleteCommentWithAuthorization(Long commentId, String username);

    boolean isUserAuthorizedToModifyComment(Long commentId, String username);
} 
