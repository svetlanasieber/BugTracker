package com.bugtracker.bugtracker.config.security;

import com.bugtracker.bugtracker.user.model.Role;
import com.bugtracker.bugtracker.user.model.User;
import com.bugtracker.bugtracker.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionService {

    private final UserService userService;

    public boolean hasRole(String roleName) {
        String roleWithPrefix = roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName;
        return getCurrentUserAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(roleWithPrefix));
    }


    public boolean hasAnyRole(String... roleNames) {
        for (String roleName : roleNames) {
            if (hasRole(roleName)) {
                return true;
            }
        }
        return false;
    }

    public Set<String> getCurrentUserRoles() {
        return getCurrentUserAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }

    public boolean isCurrentUserAdmin() {
        return hasRole("ADMIN");
    }

    public boolean isCurrentUserDeveloper() {
        return hasRole("DEVELOPER");
    }


    public boolean isCurrentUserQA() {
        return hasRole("QA");
    }

    public boolean isCurrentUserProjectManager() {
        return hasRole("PROJECT_MANAGER");
    }

    public boolean hasManagementPermissions() {
        return hasAnyRole("ADMIN", "PROJECT_MANAGER");
    }


    public boolean hasTechnicalPermissions() {
        return hasAnyRole("DEVELOPER", "QA");
    }

    /**
     * 📋 Проверява дали current user може да assign-ва bugs
     * (Administrator, Project Manager, или QA)
     */
    public boolean canAssignBugs() {
        return hasAnyRole("ADMIN", "PROJECT_MANAGER", "QA");
    }

    /**
     * 🔧 Проверява дали current user може да resolve-ва bugs
     * (Administrator или Developer)
     */
    public boolean canResolveBugs() {
        return hasAnyRole("ADMIN", "DEVELOPER");
    }

    /**
     * 👤 Проверява дали current user може да управлява потребители
     * (само Administrator)
     */
    public boolean canManageUsers() {
        return hasRole("ADMIN");
    }

    /**
     * 📊 Проверява дали current user може да управлява проекти
     * (Administrator или Project Manager)
     */
    public boolean canManageProjects() {
        return hasAnyRole("ADMIN", "PROJECT_MANAGER");
    }

    // ==================== CURRENT USER INFO ====================

    /**
     * Връща username на current user
     */
    public String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }

    /**
     * Връща current user entity
     */
    public Optional<User> getCurrentUser() {
        String username = getCurrentUsername();
        if (username == null) {
            return Optional.empty();
        }
        return userService.findByEmail(username);
    }

    /**
     * Проверява дали е authenticated user
     */
    public boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser");
    }

    // ==================== HELPER METHODS ====================

    /**
     * Връща authorities на current user
     */
    private Collection<? extends GrantedAuthority> getCurrentUserAuthorities() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getAuthorities() : Set.of();
    }

    /**
     * Логва опит за unauthorized access
     */
    public void logUnauthorizedAccess(String action) {
        String username = getCurrentUsername();
        Set<String> roles = getCurrentUserRoles();
        log.warn("🚫 Unauthorized access attempt - User: {} | Roles: {} | Action: {}", 
                username, roles, action);
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Проверява дали user може да достъпи определен проект
     * (базова проверка - може да се разшири с project-specific логика)
     */
    public boolean canAccessProject(Long projectId) {
        // Админите могат всички проекти
        if (isCurrentUserAdmin()) {
            return true;
        }
        
        // TODO: Добави логика за проверка на project membership
        // За момента всички authenticated users могат да достъпят проекти
        return isAuthenticated();
    }

    /**
     * Проверява дали user може да достъпи определен bug
     * (базова проверка - може да се разшири с bug-specific логика)
     */
    public boolean canAccessBug(Long bugId) {
        // Админите и technical roles могат всички bugs
        if (hasAnyRole("ADMIN", "DEVELOPER", "QA", "PROJECT_MANAGER")) {
            return true;
        }
        
        // TODO: Добави логика за проверка на bug assignment/ownership
        // За момента всички authenticated users могат да достъпят bugs
        return isAuthenticated();
    }

    /**
     * Проверява дали user може да редактира определен bug
     */
    public boolean canEditBug(Long bugId) {
        // Админите могат да редактират всички bugs
        if (isCurrentUserAdmin()) {
            return true;
        }
        
        // PM и QA могат да редактират bugs
        if (hasAnyRole("PROJECT_MANAGER", "QA")) {
            return true;
        }
        
        // Developers могат да редактират assigned bugs
        if (isCurrentUserDeveloper()) {
            // TODO: Проверка дали bug-а е assigned на current user
            return true;
        }
        
        return false;
    }
} 
