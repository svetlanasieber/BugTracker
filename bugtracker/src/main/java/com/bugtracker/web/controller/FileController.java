package com.bugtracker.web.controller;

import com.bugtracker.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.nio.file.Path;

@Controller
@RequestMapping("/uploads")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    private final FileService fileService;

    @GetMapping("/comments/{filename:.+}")
    public ResponseEntity<Resource> serveCommentScreenshot(@PathVariable String filename) throws IOException {
        Resource resource = fileService.loadCommentScreenshot(filename);
        
        if (resource == null) {
            return ResponseEntity.notFound().build();
        }
        
        Path filePath = fileService.getFilePath("comments", filename);
        String contentType = fileService.getContentType(filePath);
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(resource);
    }

    @GetMapping("/profiles/{filename:.+}")
    public ResponseEntity<Resource> serveProfileImage(@PathVariable String filename) throws IOException {
        Resource resource = fileService.loadProfileImage(filename);
        
        if (resource == null) {
            return ResponseEntity.notFound().build();
        }
        
        Path filePath = fileService.getFilePath("profiles", filename);
        String contentType = fileService.getContentType(filePath);
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(resource);
    }
}
