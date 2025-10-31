package com.bugtracker.service;

import com.bugtracker.config.FileStorageProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final FileStorageProperties fileStorageProperties;

    public Resource loadCommentScreenshot(String filename) throws IOException {
        Path filePath = Paths.get(fileStorageProperties.uploadDir(), "comments", filename);
        
        if (!Files.exists(filePath)) {
            log.warn("Comment screenshot not found: {}", filename);
            return null;
        }
        
        Resource resource = new UrlResource(filePath.toUri());
        
        if (!resource.exists() || !resource.isReadable()) {
            log.warn("Comment screenshot not readable: {}", filename);
            return null;
        }
        
        return resource;
    }

    public Resource loadProfileImage(String filename) throws IOException {
        Path filePath = Paths.get(fileStorageProperties.uploadDir(), "profiles", filename);
        
        if (!Files.exists(filePath)) {
            log.warn("Profile image not found: {}", filename);
            return null;
        }
        
        Resource resource = new UrlResource(filePath.toUri());
        
        if (!resource.exists() || !resource.isReadable()) {
            log.warn("Profile image not readable: {}", filename);
            return null;
        }
        
        return resource;
    }

    public String getContentType(Path filePath) throws IOException {
        String contentType = Files.probeContentType(filePath);
        return contentType != null ? contentType : MediaType.APPLICATION_OCTET_STREAM_VALUE;
    }

    public Path getFilePath(String folder, String filename) {
        return Paths.get(fileStorageProperties.uploadDir(), folder, filename);
    }
}

