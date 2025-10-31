package com.bugtracker.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "app.file-storage")
public record FileStorageProperties(
    
    
    @DefaultValue("uploads")
    String uploadDir,
    
    
    @DefaultValue("10485760") 
    long maxFileSize,
    
    
    @DefaultValue("jpg,jpeg,png,gif,pdf,txt,doc,docx")
    String allowedExtensions
) {
    
    
    public String[] getAllowedExtensionsArray() {
        return allowedExtensions.split(",");
    }
    
    
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