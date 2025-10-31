package com.bugtracker.util;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class DatabaseFixUtil {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseFixUtil.class);
    private final DataSource dataSource;

    public DatabaseFixUtil(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void executeSqlScript(String resourcePath) {
        try {
            
            ClassPathResource resource = new ClassPathResource(resourcePath);
            List<String> sqlStatements = new ArrayList<>();
            
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream()))) {
                
                StringBuilder sqlStatement = new StringBuilder();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    
                    if (line.trim().isEmpty() || line.trim().startsWith("--")) {
                        continue;
                    }
                    
                    sqlStatement.append(line).append(" ");
                    
                    
                    if (line.trim().endsWith(";")) {
                        sqlStatements.add(sqlStatement.toString());
                        sqlStatement = new StringBuilder();
                    }
                }
            }
            
            
            try (Connection connection = dataSource.getConnection()) {
                connection.setAutoCommit(false);
                
                try (Statement statement = connection.createStatement()) {
                    for (String sql : sqlStatements) {
                        logger.info("Executing SQL: {}", sql);
                        statement.execute(sql);
                    }
                    
                    connection.commit();
                    logger.info("SQL script executed successfully: {}", resourcePath);
                } catch (Exception e) {
                    connection.rollback();
                    logger.error("Error executing SQL script: {}", e.getMessage(), e);
                    throw e;
                }
            }
        } catch (Exception e) {
            logger.error("Failed to execute SQL script {}: {}", resourcePath, e.getMessage(), e);
        }
    }
} 