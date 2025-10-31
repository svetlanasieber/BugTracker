package com.bugtracker.web.controller;

import com.bugtracker.config.FileStorageProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/uploads")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    private final FileStorageProperties fileStorageProperties;

    
    @GetMapping("/comments/{filename:.+}")
    public ResponseEntity<Resource> serveCommentScreenshot(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(fileStorageProperties.uploadDir(), "comments", filename);
            
            if (!Files.exists(filePath)) {
                log.warn("Comment screenshot not found: {}", filename);
                return ResponseEntity.notFound().build();
            }
            
            Resource resource = new UrlResource(filePath.toUri());
            
            if (!resource.exists() || !resource.isReadable()) {
                log.warn("Comment screenshot not readable: {}", filename);
                return ResponseEntity.notFound().build();
            }
            
            
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);
                    
        } catch (IOException e) {
            log.error("Error serving comment screenshot {}: {}", filename, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    
    @GetMapping("/profiles/{filename:.+}")
    public ResponseEntity<Resource> serveProfileImage(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(fileStorageProperties.uploadDir(), "profiles", filename);
            
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }
            
            Resource resource = new UrlResource(filePath.toUri());
            
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }
            
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);
                    
        } catch (IOException e) {
            log.error("Error serving profile image {}: {}", filename, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}

