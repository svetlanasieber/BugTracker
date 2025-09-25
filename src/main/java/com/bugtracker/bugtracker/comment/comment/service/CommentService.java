package com.bugtracker.bugtracker.comment.service;

import com.bugtracker.bugtracker.comment.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CommentService {
    
    Comment createComment(Long bugId, Long authorId, String content);
    
    Comment getComment(Long commentId);
    
    List<Comment> getCommentsByBugId(Long bugId);
    
    Page<Comment> getCommentsByBugId(Long bugId, Pageable pageable);
    
    Comment updateComment(Long commentId, String content);
    
    void deleteComment(Long commentId);
    
    void deleteAllCommentsForBug(Long bugId);
    
    // ============= NEW BUSINESS LOGIC METHODS =============
    
    /**
     * Creates a comment with the current authenticated user as author.
     */
    Comment createCommentWithCurrentUser(Long bugId, String content, String username);
    
    /**
     * Updates a comment with authorization check.
     * Only the author or admin can update comments.
     */
    Comment updateCommentWithAuthorization(Long commentId, String content, String username);
    
    /**
     * Deletes a comment with authorization check.
     * Only the author or admin can delete comments.
     */
    void deleteCommentWithAuthorization(Long commentId, String username);
    
    /**
     * Checks if a user is authorized to modify a comment.
     * Returns true if the user is the author or an admin.
     */
    boolean isUserAuthorizedToModifyComment(Long commentId, String username);
    
    /**
     * Creates a comment with optional screenshot attachment.
     * Handles file upload and storage automatically.
     */
    Comment createCommentWithScreenshot(Long bugId, String content, MultipartFile screenshot, String username);
} 