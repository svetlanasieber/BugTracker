package com.bugtracker.bugtracker.config.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Java-based Flyway migration to create necessary file storage directories.
 */
public class V3__AddFileStorageFolders extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        // Get the user's home directory
        String userHome = System.getProperty("user.home");
        
        // Create the main upload directory
        Path uploadDir = Paths.get(userHome, "bugtracker", "uploads");
        createDirectoryIfNotExists(uploadDir);
        
        // Create profile pictures directory
        Path profilesDir = Paths.get(userHome, "bugtracker", "uploads", "profiles");
        createDirectoryIfNotExists(profilesDir);
        
        // Create bug attachments directory
        Path attachmentsDir = Paths.get(userHome, "bugtracker", "uploads", "attachments");
        createDirectoryIfNotExists(attachmentsDir);
        
        // Create temporary files directory
        Path tempDir = Paths.get(userHome, "bugtracker", "uploads", "temp");
        createDirectoryIfNotExists(tempDir);
        
        System.out.println("✅ File storage directories created successfully");
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