package com.bugtracker.bugtracker.web.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Controller
public class FixController {

    private final DataSource dataSource;

    public FixController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/fix-database")
    @ResponseBody
    public String fixDatabase() {
        StringBuilder result = new StringBuilder();
        
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            
            try (Statement statement = connection.createStatement()) {
              
                statement.execute("INSERT IGNORE INTO users (first_name, last_name, email, password, created_at, updated_at, is_active) "
                    + "VALUES ('Admin', 'User', 'admin@bugtracker.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', NOW(), NOW(), TRUE)");
                result.append("✓ Admin user created or verified<br>");
                
              
                statement.execute("INSERT IGNORE INTO roles (name) VALUES ('USER')");
                statement.execute("INSERT IGNORE INTO roles (name) VALUES ('ADMIN')");
                result.append("✓ Roles created or verified<br>");
                
              
                statement.execute("INSERT IGNORE INTO users_roles (user_id, role_id) "
                    + "SELECT (SELECT id FROM users WHERE email = 'admin@bugtracker.com'), (SELECT id FROM roles WHERE name = 'ADMIN')");
                result.append("✓ Admin role assigned to admin user<br>");
                
              
                statement.execute("INSERT INTO projects (name, description, is_active, created_at, updated_at) "
                    + "SELECT 'Bug Tracker Development', 'Internal project for developing the bug tracking application', TRUE, NOW(), NOW() "
                    + "WHERE NOT EXISTS (SELECT 1 FROM projects LIMIT 1)");
                
         
                statement.execute("INSERT INTO projects (name, description, is_active, created_at, updated_at) "
                    + "SELECT 'Website Redesign', 'Project to redesign and modernize the company website', TRUE, NOW(), NOW() "
                    + "WHERE (SELECT COUNT(*) FROM projects) = 1");
                result.append("✓ Sample projects created (if needed)<br>");
                
               
                statement.execute("INSERT IGNORE INTO project_members (project_id, user_id) "
                    + "SELECT p.id, u.id FROM projects p, users u "
                    + "WHERE u.email = 'admin@bugtracker.com' "
                    + "AND NOT EXISTS (SELECT 1 FROM project_members pm WHERE pm.project_id = p.id AND pm.user_id = u.id)");
                result.append("✓ Admin user added to projects<br>");
                
                connection.commit();
                result.append("<br><strong>Database fix completed successfully!</strong><br>");
                result.append("<br>Return to <a href='/projects'>Projects Page</a>");
            } catch (Exception e) {
                connection.rollback();
                result.append("Error: ").append(e.getMessage());
                throw e;
            }
        } catch (Exception e) {
            result.append("Database connection error: ").append(e.getMessage());
        }
        
        return "<html><body style='font-family: Arial; padding: 20px;'>" +
               "<h2>BugTracker Database Fix Utility</h2>" +
               "<div style='background: #f5f5f5; padding: 15px; border-radius: 5px;'>" +
               result.toString() +
               "</div></body></html>";
    }

    @GetMapping("/fix-admin-roles")
    @ResponseBody
    public String fixAdminRoles() {
        StringBuilder result = new StringBuilder();
        
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            
            try (Statement statement = connection.createStatement()) {
               
                statement.execute("INSERT IGNORE INTO roles (name) VALUES ('ADMIN')");
                
             
                statement.execute("INSERT IGNORE INTO roles (name) VALUES ('USER')");
                
                
                statement.execute("UPDATE roles SET name = 'ADMIN' WHERE name = 'ROLE_ADMIN'");
                statement.execute("UPDATE roles SET name = 'USER' WHERE name = 'ROLE_USER'");
                
                result.append("✓ Roles fixed to use correct format<br>");
                
              
                statement.execute("INSERT IGNORE INTO users (first_name, last_name, email, password, created_at, updated_at, is_active) " +
                    "VALUES ('Admin', 'User', 'admin@bugtracker.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', NOW(), NOW(), TRUE)");
                
                result.append("✓ Admin user created or verified<br>");
                
              
                statement.execute("INSERT IGNORE INTO users_roles (user_id, role_id) " +
                    "SELECT u.id, r.id FROM users u, roles r " +
                    "WHERE u.email = 'admin@bugtracker.com' AND r.name = 'ADMIN'");
                
                result.append("✓ Admin role assigned to admin user<br>");
                
                connection.commit();
                result.append("<br><strong>Admin roles fixed successfully!</strong><br>");
                result.append("<br>Return to <a href='/projects'>Projects Page</a>");
            } catch (Exception e) {
                connection.rollback();
                result.append("Error: ").append(e.getMessage());
                throw e;
            }
        } catch (Exception e) {
            result.append("Database connection error: ").append(e.getMessage());
        }
        
        return "<html><body style='font-family: Arial; padding: 20px;'>" +
               "<h2>BugTracker Admin Role Fix Utility</h2>" +
               "<div style='background: #f5f5f5; padding: 15px; border-radius: 5px;'>" +
               result.toString() +
               "</div></body></html>";
    }
} 
