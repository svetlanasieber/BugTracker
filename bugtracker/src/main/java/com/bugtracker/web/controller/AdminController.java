package com.bugtracker.web.controller;

import com.bugtracker.config.security.PermissionService;
import com.bugtracker.config.security.annotations.*;
import com.bugtracker.user.model.Role;
import com.bugtracker.user.model.User;
import com.bugtracker.user.repository.RoleRepository;
import com.bugtracker.user.service.UserService;
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

@Controller
@RequestMapping("/admin")
@AuthorizeAdmin 
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final UserService userService;
    private final RoleRepository roleRepository;
    private final PermissionService permissionService;

    

    
    @GetMapping
    public String adminDashboard(Model model) {
        log.info("Admin accessing dashboard: {}", permissionService.getCurrentUsername());
        
        
        List<User> allUsers = userService.findAllUsers();
        List<Role> allRoles = roleRepository.findAll();
        
        model.addAttribute("totalUsers", allUsers.size());
        model.addAttribute("totalRoles", allRoles.size());
        model.addAttribute("currentUser", permissionService.getCurrentUser().orElse(null));
        model.addAttribute("userRoles", permissionService.getCurrentUserRoles());
        
        
        Map<String, Long> roleBreakdown = allUsers.stream()
                .flatMap(user -> user.getRoles().stream())
                .collect(Collectors.groupingBy(Role::getName, Collectors.counting()));
        model.addAttribute("roleBreakdown", roleBreakdown);
        
        return "admin/dashboard";
    }

    

    
    @GetMapping("/users")
    public String userManagement(Model model) {
        log.info("Admin accessing user management: {}", permissionService.getCurrentUsername());
        
        List<User> users = userService.findAllUsers();
        List<Role> roles = roleRepository.findAll();
        
        model.addAttribute("users", users);
        model.addAttribute("allRoles", roles);
        model.addAttribute("permissionService", permissionService);
        
        return "admin/users";
    }

    
    @GetMapping("/users/create")
    public String createUserForm(Model model) {
        List<Role> roles = roleRepository.findAll();
        model.addAttribute("allRoles", roles);
        return "admin/create-user";
    }

    
    @PostMapping("/users/create")
    public ResponseEntity<Map<String, Object>> createUser(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam(required = false) List<String> roleNames) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            log.info("Admin creating user: {} by {}", email, permissionService.getCurrentUsername());
            
            
            if (userService.existsByEmail(email)) {
                response.put("success", false);
                response.put("message", "User with this email already exists");
                return ResponseEntity.badRequest().body(response);
            }
            
            
            User newUser = User.builder()
                    .firstName(firstName)
                    .lastName(lastName)
                    .email(email)
                    .password(password) 
                    .isActive(true)
                    .build();
            
            
            User savedUser = userService.createUserWithRoles(firstName, lastName, email, password, roleNames);
            
            response.put("success", true);
            response.put("message", "User created successfully");
            response.put("userId", savedUser.getId());
            response.put("userEmail", savedUser.getEmail());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error creating user: {}", e.getMessage());
            response.put("success", false);
            response.put("message", "Error creating user: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    

    
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
            log.error("Error updating user role: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", 
                    "Error updating user role: " + e.getMessage());
        }
        
        return "redirect:/admin/users";
    }

    
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
            log.error("Error assigning role: {}", e.getMessage());
            response.put("success", false);
            response.put("message", "Error assigning role: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    

    
    @AuthorizeBugResolution
    @PostMapping("/demo/resolve-bug/{bugId}")
    @ResponseBody
    public Map<String, Object> resolveBugDemo(@PathVariable Long bugId) {
        Map<String, Object> response = new HashMap<>();
        
        
        if (!permissionService.canResolveBugs()) {
            permissionService.logUnauthorizedAccess("resolve-bug-demo");
            response.put("error", "Access denied - insufficient permissions");
            return response;
        }
        
        log.info("Bug resolution demo for bug {} by {}", bugId, permissionService.getCurrentUsername());
        response.put("message", "Bug " + bugId + " resolved successfully");
        response.put("resolver", permissionService.getCurrentUsername());
        response.put("permissions", permissionService.getCurrentUserRoles());
        
        return response;
    }

    
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
        
        log.info("Bug assignment demo: bug {} to developer {} by {}",
                bugId, developerId, permissionService.getCurrentUsername());
        response.put("message", "Bug " + bugId + " assigned to developer " + developerId);
        response.put("assigner", permissionService.getCurrentUsername());
        
        return response;
    }

    
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

    
    @AuthorizeTechnical
    @GetMapping("/demo/technical-info")
    @ResponseBody
    public Map<String, Object> technicalDemo() {
        Map<String, Object> response = new HashMap<>();
        
        log.info("Technical demo accessed by {}", permissionService.getCurrentUsername());
        response.put("message", "Technical information accessed");
        response.put("hasTechnicalPermissions", permissionService.hasTechnicalPermissions());
        response.put("isDeveloper", permissionService.isCurrentUserDeveloper());
        response.put("isQA", permissionService.isCurrentUserQA());
        
        return response;
    }

    

    
    @GetMapping("/system-info")
    @ResponseBody
    public Map<String, Object> systemInfo() {
        Map<String, Object> info = new HashMap<>();
        
        
        info.put("currentUser", permissionService.getCurrentUsername());
        info.put("userRoles", permissionService.getCurrentUserRoles());
        info.put("isAuthenticated", permissionService.isAuthenticated());
        
        
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
        
        
        info.put("systemStats", Map.of(
            "totalUsers", userService.findAllUsers().size(),
            "totalRoles", roleRepository.findAll().size()
        ));
        
        log.info("System info requested by {}", permissionService.getCurrentUsername());
        return info;
    }
} 