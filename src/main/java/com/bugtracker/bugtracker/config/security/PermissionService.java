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


    public boolean canAssignBugs() {
        return hasAnyRole("ADMIN", "PROJECT_MANAGER", "QA");
    }


    public boolean canResolveBugs() {
        return hasAnyRole("ADMIN", "DEVELOPER");
    }


    public boolean canManageUsers() {
        return hasRole("ADMIN");
    }


    public boolean canManageProjects() {
        return hasAnyRole("ADMIN", "PROJECT_MANAGER");
    }


    public String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }


    public Optional<User> getCurrentUser() {
        String username = getCurrentUsername();
        if (username == null) {
            return Optional.empty();
        }
        return userService.findByEmail(username);
    }


    public boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser");
    }


    private Collection<? extends GrantedAuthority> getCurrentUserAuthorities() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getAuthorities() : Set.of();
    }


    public void logUnauthorizedAccess(String action) {
        String username = getCurrentUsername();
        Set<String> roles = getCurrentUserRoles();
        log.warn("Unauthorized access attempt - User: {} | Roles: {} | Action: {}", 
                username, roles, action);
    }

 
    public boolean canAccessProject(Long projectId) {
  
        if (isCurrentUserAdmin()) {
            return true;
        }
        

        return isAuthenticated();
    }


    public boolean canAccessBug(Long bugId) {
       
        if (hasAnyRole("ADMIN", "DEVELOPER", "QA", "PROJECT_MANAGER")) {
            return true;
        }

        return isAuthenticated();
    }


    public boolean canEditBug(Long bugId) {
     
        if (isCurrentUserAdmin()) {
            return true;
        }
        
    
        if (hasAnyRole("PROJECT_MANAGER", "QA")) {
            return true;
        }
        
       
        if (isCurrentUserDeveloper()) {
      
            return true;
        }
        
        return false;
    }
} 
