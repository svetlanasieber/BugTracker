package com.bugtracker.bugtracker.web.controller;

import com.bugtracker.bugtracker.config.security.PermissionService;
import com.bugtracker.bugtracker.config.security.annotations.*;
import com.bugtracker.bugtracker.user.model.Role;
import com.bugtracker.bugtracker.user.model.User;
import com.bugtracker.bugtracker.user.repository.RoleRepository;
import com.bugtracker.bugtracker.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 👑 AdminController - Демонстрира новите security features
 * 
 * Този controller показва как се използват:
 * - Custom security annotations
 * - PermissionService за programmatic проверки
 * - Role-based access control
 * - User management functionality
 */
@Controller
@RequestMapping("/admin")
@AuthorizeAdmin // Целият controller е достъпен само за админи
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final UserService userService;
    private final RoleRepository roleRepository;
    private final PermissionService permissionService;

    // ==================== DASHBOARD ====================

    /**
     * 📊 Admin Dashboard - показва обща информация за системата
     */
    @GetMapping
    public String adminDashboard(Model model) {
        log.info("🏠 Admin accessing dashboard: {}", permissionService.getCurrentUsername());
        
        // Статистики за dashboard
        List<User> allUsers = userService.findAllUsers();
        List<Role> allRoles = roleRepository.findAll();
        
        model.addAttribute("totalUsers", allUsers.size());
        model.addAttribute("totalRoles", allRoles.size());
        model.addAttribute("currentUser", permissionService.getCurrentUser().orElse(null));
        model.addAttribute("userRoles", permissionService.getCurrentUserRoles());
        
        // Breakdown по роли
        Map<String, Long> roleBreakdown = allUsers.stream()
                .flatMap(user -> user.getRoles().stream())
                .collect(Collectors.groupingBy(Role::getName, Collectors.counting()));
        model.addAttribute("roleBreakdown", roleBreakdown);
        
        return "admin/dashboard";
    }

    // ==================== USER MANAGEMENT ====================

    /**
     * 👥 User Management Page
     */
    @GetMapping("/users")
    public String userManagement(Model model) {
        log.info("👥 Admin accessing user management: {}", permissionService.getCurrentUsername());
        
        List<User> users = userService.findAllUsers();
        List<Role> roles = roleRepository.findAll();
        
        model.addAttribute("users", users);
        model.addAttribute("allRoles", roles);
        model.addAttribute("permissionService", permissionService);
        
        return "admin/users";
    }

    /**
     * 🆕 Create User Form
     */
    @GetMapping("/users/create")
    public String createUserForm(Model model) {
        List<Role> roles = roleRepository.findAll();
        model.addAttribute("allRoles", roles);
        return "admin/create-user";
    }

    /**
     * 💾 Create User (Admin-controlled creation)
     */
    @PostMapping("/users/create")
    public ResponseEntity<Map<String, Object>> createUser(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam(required = false) List<String> roleNames) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            log.info("🆕 Admin creating user: {} by {}", email, permissionService.getCurrentUsername());
            
            // Validate input
            if (userService.existsByEmail(email)) {
                response.put("success", false);
                response.put("message", "User with this email already exists");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Create user entity
            User newUser = User.builder()
                    .firstName(firstName)
                    .lastName(lastName)
                    .email(email)
                    .password(password) // UserService ще encode-не паролата
                    .isActive(true)
                    .build();
            
            // Създаване на потребителя чрез UserService
            User savedUser = userService.createUserWithRoles(firstName, lastName, email, password, roleNames);
            
            response.put("success", true);
            response.put("message", "User created successfully");
            response.put("userId", savedUser.getId());
            response.put("userEmail", savedUser.getEmail());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error creating user: {}", e.getMessage());
            response.put("success", false);
            response.put("message", "Error creating user: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== ROLE MANAGEMENT ====================

    /**
     * 🎭 Toggle role for user (used by HTML form)
     */
    @PostMapping("/users/{userId}/role")
    public String toggleUserRole(
            @PathVariable Long userId,
            @RequestParam String roleName,
            RedirectAttributes redirectAttributes) {
        
        try {
            log.info("🎭 Admin toggling role {} for user {} by {}", 
                    roleName, userId, permissionService.getCurrentUsername());
            
            userService.changeUserRole(userId, roleName);
            
            redirectAttributes.addFlashAttribute("success", 
                    "User role updated successfully");
            
        } catch (Exception e) {
            log.error("❌ Error updating user role: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", 
                    "Error updating user role: " + e.getMessage());
        }
        
        return "redirect:/admin/users";
    }

    /**
     * 🎭 Assign role to user (API endpoint)
     */
    @PostMapping("/users/{userId}/roles/{roleName}")
    public ResponseEntity<Map<String, Object>> assignRole(
            @PathVariable Long userId, 
            @PathVariable String roleName) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            log.info("🎭 Admin assigning role {} to user {} by {}", 
                    roleName, userId, permissionService.getCurrentUsername());
            
            userService.changeUserRole(userId, roleName);
            
            response.put("success", true);
            response.put("message", "Role assigned successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error assigning role: {}", e.getMessage());
            response.put("success", false);
            response.put("message", "Error assigning role: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== PERMISSION DEMONSTRATIONS ====================

    /**
     * 🔧 Bug Resolution Demo (само Admin и Developer)
     */
    @AuthorizeBugResolution
    @PostMapping("/demo/resolve-bug/{bugId}")
    @ResponseBody
    public Map<String, Object> resolveBugDemo(@PathVariable Long bugId) {
        Map<String, Object> response = new HashMap<>();
        
        // Programmatic проверка (не е нужна заради @AuthorizeBugResolution, но за демо)
        if (!permissionService.canResolveBugs()) {
            permissionService.logUnauthorizedAccess("resolve-bug-demo");
            response.put("error", "Access denied - insufficient permissions");
            return response;
        }
        
        log.info("🔧 Bug resolution demo for bug {} by {}", bugId, permissionService.getCurrentUsername());
        response.put("message", "Bug " + bugId + " resolved successfully");
        response.put("resolver", permissionService.getCurrentUsername());
        response.put("permissions", permissionService.getCurrentUserRoles());
        
        return response;
    }

    /**
     * 📋 Bug Assignment Demo (Admin, PM, QA)
     */
    @AuthorizeBugAssignment
    @PostMapping("/demo/assign-bug/{bugId}")
    @ResponseBody
    public Map<String, Object> assignBugDemo(@PathVariable Long bugId, @RequestParam Long developerId) {
        Map<String, Object> response = new HashMap<>();
        
        if (!permissionService.canAssignBugs()) {
            permissionService.logUnauthorizedAccess("assign-bug-demo");
            response.put("error", "Access denied - insufficient permissions");
            return response;
        }
        
        log.info("📋 Bug assignment demo: bug {} to developer {} by {}", 
                bugId, developerId, permissionService.getCurrentUsername());
        response.put("message", "Bug " + bugId + " assigned to developer " + developerId);
        response.put("assigner", permissionService.getCurrentUsername());
        
        return response;
    }

    /**
     * 🏢 Management Demo (Admin, PM)
     */
    @AuthorizeManagement
    @GetMapping("/demo/management-info")
    @ResponseBody
    public Map<String, Object> managementDemo() {
        Map<String, Object> response = new HashMap<>();
        
        log.info("🏢 Management demo accessed by {}", permissionService.getCurrentUsername());
        response.put("message", "Management information accessed");
        response.put("hasManagementPermissions", permissionService.hasManagementPermissions());
        response.put("canManageProjects", permissionService.canManageProjects());
        response.put("canManageUsers", permissionService.canManageUsers());
        
        return response;
    }

    /**
     * ⚙️ Technical Demo (Developer, QA)
     */
    @AuthorizeTechnical
    @GetMapping("/demo/technical-info")
    @ResponseBody
    public Map<String, Object> technicalDemo() {
        Map<String, Object> response = new HashMap<>();
        
        log.info("⚙️ Technical demo accessed by {}", permissionService.getCurrentUsername());
        response.put("message", "Technical information accessed");
        response.put("hasTechnicalPermissions", permissionService.hasTechnicalPermissions());
        response.put("isDeveloper", permissionService.isCurrentUserDeveloper());
        response.put("isQA", permissionService.isCurrentUserQA());
        
        return response;
    }

    // ==================== SYSTEM INFO ====================

    /**
     * 📊 System Debug Info
     */
    @GetMapping("/system-info")
    @ResponseBody
    public Map<String, Object> systemInfo() {
        Map<String, Object> info = new HashMap<>();
        
        // Current user info
        info.put("currentUser", permissionService.getCurrentUsername());
        info.put("userRoles", permissionService.getCurrentUserRoles());
        info.put("isAuthenticated", permissionService.isAuthenticated());
        
        // Permission checks
        info.put("permissions", Map.of(
            "isAdmin", permissionService.isCurrentUserAdmin(),
            "isDeveloper", permissionService.isCurrentUserDeveloper(),
            "isQA", permissionService.isCurrentUserQA(),
            "isProjectManager", permissionService.isCurrentUserProjectManager(),
            "hasManagementPermissions", permissionService.hasManagementPermissions(),
            "hasTechnicalPermissions", permissionService.hasTechnicalPermissions(),
            "canAssignBugs", permissionService.canAssignBugs(),
            "canResolveBugs", permissionService.canResolveBugs(),
            "canManageUsers", permissionService.canManageUsers(),
            "canManageProjects", permissionService.canManageProjects()
        ));
        
        // System stats
        info.put("systemStats", Map.of(
            "totalUsers", userService.findAllUsers().size(),
            "totalRoles", roleRepository.findAll().size()
        ));
        
        log.info("📊 System info requested by {}", permissionService.getCurrentUsername());
        return info;
    }
} 