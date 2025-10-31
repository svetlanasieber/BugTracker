package com.bugtracker.comment.service;

import com.bugtracker.bug.model.Bug;
import com.bugtracker.comment.model.Comment;
import com.bugtracker.user.model.User;
import com.bugtracker.bug.repository.BugRepository;
import com.bugtracker.comment.repository.CommentRepository;
import com.bugtracker.user.repository.UserRepository;
import com.bugtracker.config.FileStorageProperties;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final BugRepository bugRepository;
    private final UserRepository userRepository;
    private final FileStorageProperties fileStorageProperties;

    @Override
    @Transactional
    public Comment createComment(Long bugId, Long authorId, String content) {
        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new IllegalArgumentException("Bug not found with id: " + bugId));
        
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + authorId));
        
        Comment comment = new Comment();
        comment.setBug(bug);
        comment.setAuthor(author);
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());
        
        return commentRepository.save(comment);
    }

    @Override
    public Comment getComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found with id: " + commentId));
    }

    @Override
    public List<Comment> getCommentsByBugId(Long bugId) {
        return commentRepository.findByBugId(bugId);
    }

    @Override
    public Page<Comment> getCommentsByBugId(Long bugId, Pageable pageable) {
        return commentRepository.findByBugId(bugId, pageable);
    }

    @Override
    @Transactional
    public Comment updateComment(Long commentId, String content) {
        Comment comment = getComment(commentId);
        comment.setContent(content);
        comment.setUpdatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = getComment(commentId);
        commentRepository.delete(comment);
    }

    @Override
    @Transactional
    public void deleteAllCommentsForBug(Long bugId) {
        commentRepository.deleteAllByBugId(bugId);
    }
    
    
    
    @Override
    @Transactional
    public Comment createCommentWithCurrentUser(Long bugId, String content, String username) {
        User currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + username));
        
        Comment comment = createComment(bugId, currentUser.getId(), content);
        log.info("Comment created by user {} for bug {}", username, bugId);
        return comment;
    }
    
    @Override
    @Transactional
    public Comment updateCommentWithAuthorization(Long commentId, String content, String username) {
        if (!isUserAuthorizedToModifyComment(commentId, username)) {
            throw new RuntimeException("You can only edit your own comments!");
        }
        
        Comment updatedComment = updateComment(commentId, content);
        log.info("Comment {} updated by user {}", commentId, username);
        return updatedComment;
    }
    
    @Override
    @Transactional
    public void deleteCommentWithAuthorization(Long commentId, String username) {
        if (!isUserAuthorizedToModifyComment(commentId, username)) {
            throw new RuntimeException("You can only delete your own comments!");
        }
        
        deleteComment(commentId);
        log.info("Comment {} deleted by user {}", commentId, username);
    }
    
    @Override
    public boolean isUserAuthorizedToModifyComment(Long commentId, String username) {
        User currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + username));
        
        Comment comment = getComment(commentId);
        
        
        return comment.getAuthor().getId().equals(currentUser.getId()) || 
               currentUser.getRoles().stream().anyMatch(role -> "ROLE_ADMIN".equals(role.getName()));
    }
    
    @Override
    @Transactional
    public Comment createCommentWithScreenshot(Long bugId, String content, MultipartFile screenshot, String username) {
        User author = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + username));
        
        Bug bug = bugRepository.findById(bugId)
                .orElseThrow(() -> new IllegalArgumentException("Bug not found with id: " + bugId));
        
        Comment comment = Comment.builder()
                .bug(bug)
                .author(author)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();
        
        
        if (screenshot != null && !screenshot.isEmpty()) {
            
            validateScreenshot(screenshot);
            
            try {
                String savedFilename = saveScreenshot(screenshot);
                comment.setAttachmentFilename(savedFilename);
                comment.setAttachmentOriginalName(screenshot.getOriginalFilename());
                log.info("Screenshot attached to comment: {}", savedFilename);
            } catch (IOException e) {
                log.error("Error uploading screenshot: {}", e.getMessage());
                throw new RuntimeException("Failed to upload screenshot: " + e.getMessage());
            }
        }
        
        Comment savedComment = commentRepository.save(comment);
        log.info("Comment created for bug {} by user {} with attachment: {}", 
                bugId, username, comment.getAttachmentFilename() != null);
        
        return savedComment;
    }
    
    private String saveScreenshot(MultipartFile file) throws IOException {
        
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        String savedFilename = UUID.randomUUID().toString() + extension;
        
        
        Path uploadsDir = Paths.get(fileStorageProperties.uploadDir(), "comments");
        if (!Files.exists(uploadsDir)) {
            Files.createDirectories(uploadsDir);
        }
        
        
        Path targetPath = uploadsDir.resolve(savedFilename);
        Files.copy(file.getInputStream(), targetPath);
        
        log.info("Screenshot saved: {} (original: {})", savedFilename, originalFilename);
        return savedFilename;
    }
    
    private void validateScreenshot(MultipartFile file) {
        
        long maxSize = 10 * 1024 * 1024; 
        if (file.getSize() > maxSize) {
            throw new RuntimeException("File size exceeds maximum allowed size of 10MB");
        }
        
        
        String contentType = file.getContentType();
        String[] allowedTypes = {"image/jpeg", "image/png", "image/gif", "image/webp"};
        
        if (contentType == null) {
            throw new RuntimeException("Unable to determine file type");
        }
        
        boolean isAllowedType = false;
        for (String allowedType : allowedTypes) {
            if (allowedType.equals(contentType)) {
                isAllowedType = true;
                break;
            }
        }
        
        if (!isAllowedType) {
            throw new RuntimeException("File type not supported. Please upload JPEG, PNG, GIF, or WebP images");
        }
    }
} 