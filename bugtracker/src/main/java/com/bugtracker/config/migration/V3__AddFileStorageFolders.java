package com.bugtracker.config.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class V3__AddFileStorageFolders extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        
        String userHome = System.getProperty("user.home");
        
        
        Path uploadDir = Paths.get(userHome, "bugtracker", "uploads");
        createDirectoryIfNotExists(uploadDir);
        
        
        Path profilesDir = Paths.get(userHome, "bugtracker", "uploads", "profiles");
        createDirectoryIfNotExists(profilesDir);
        
        
        Path attachmentsDir = Paths.get(userHome, "bugtracker", "uploads", "attachments");
        createDirectoryIfNotExists(attachmentsDir);
        
        
        Path tempDir = Paths.get(userHome, "bugtracker", "uploads", "temp");
        createDirectoryIfNotExists(tempDir);
        
        System.out.println("File storage directories created successfully");
    }
    
    private void createDirectoryIfNotExists(Path dir) throws IOException {
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
            System.out.println("Created directory: " + dir);
        } else {
            System.out.println("Directory already exists: " + dir);
        }
    }
} 