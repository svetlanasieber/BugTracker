package com.bugtracker.comment.service;

import com.bugtracker.comment.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface CommentService {
    Comment createComment(UUID bugId, Long authorId, String content);
    Comment getComment(Long commentId);
    List<Comment> getCommentsByBugId(UUID bugId);
    Page<Comment> getCommentsByBugId(UUID bugId, Pageable pageable);
    Comment updateComment(Long commentId, String content);
    void deleteComment(Long commentId);
    void deleteAllCommentsForBug(UUID bugId);
    Comment createCommentWithCurrentUser(UUID bugId, String content, String username);
    Comment updateCommentWithAuthorization(Long commentId, String content, String username);
    void deleteCommentWithAuthorization(Long commentId, String username);
    boolean isUserAuthorizedToModifyComment(Long commentId, String username);
    Comment createCommentWithScreenshot(UUID bugId, String content, MultipartFile screenshot, String username);
} 