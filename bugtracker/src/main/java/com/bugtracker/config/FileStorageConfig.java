package com.bugtracker.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@EnableConfigurationProperties(FileStorageProperties.class)
@RequiredArgsConstructor
@Slf4j
public class FileStorageConfig {

    private final FileStorageProperties fileStorageProperties;

    @Bean
    public String uploadDirectoryPath() throws IOException {
        String uploadDir = fileStorageProperties.uploadDir();
        
        
        Path mainUploadDir = Paths.get(uploadDir);
        if (!Files.exists(mainUploadDir)) {
            Files.createDirectories(mainUploadDir);
            log.info("Created main upload directory: {}", mainUploadDir);
        }

        
        Path profilesDir = Paths.get(uploadDir, "profiles");
        if (!Files.exists(profilesDir)) {
            Files.createDirectories(profilesDir);
            log.info("Created profiles directory: {}", profilesDir);
        }

        
        Path attachmentsDir = Paths.get(uploadDir, "attachments");
        if (!Files.exists(attachmentsDir)) {
            Files.createDirectories(attachmentsDir);
            log.info("Created attachments directory: {}", attachmentsDir);
        }
        
        log.info("File storage configured with max size: {} bytes, allowed extensions: {}", 
                fileStorageProperties.maxFileSize(), fileStorageProperties.allowedExtensions());
        
        return uploadDir;
    }
} 