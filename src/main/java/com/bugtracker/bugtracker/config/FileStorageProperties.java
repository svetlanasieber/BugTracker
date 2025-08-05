package com.bugtracker.bugtracker.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Configuration properties for file storage settings.
 * 
 * @ConfigurationProperties provides type-safe configuration properties
 * with validation support and IDE auto-completion.
 */
@ConfigurationProperties(prefix = "app.file-storage")
public record FileStorageProperties(
    
    /**
     * Base directory for uploaded files.
     * Defaults to "uploads" if not specified.
     */
    @DefaultValue("uploads")
    String uploadDir,
    
    /**
     * Maximum file size in bytes.
     * Defaults to 10MB if not specified.
     */
    @DefaultValue("10485760") // 10MB = 10 * 1024 * 1024
    long maxFileSize,
    
    /**
     * Allowed file extensions for uploads.
     * Defaults to common image formats.
     */
    @DefaultValue("jpg,jpeg,png,gif,pdf,txt,doc,docx")
    String allowedExtensions
) {
    
    /**
     * Returns array of allowed file extensions.
     */
    public String[] getAllowedExtensionsArray() {
        return allowedExtensions.split(",");
    }
    
    /**
     * Checks if the given file extension is allowed.
     */
    public boolean isExtensionAllowed(String extension) {
        if (extension == null) return false;
        
        String normalizedExt = extension.toLowerCase().replace(".", "");
        String[] allowed = getAllowedExtensionsArray();
        
        for (String allowedExt : allowed) {
            if (allowedExt.trim().toLowerCase().equals(normalizedExt)) {
                return true;
            }
        }
        return false;
    }
} 