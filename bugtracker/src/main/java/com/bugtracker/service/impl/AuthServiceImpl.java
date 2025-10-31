package com.bugtracker.service.impl;

import com.bugtracker.user.model.User;
import com.bugtracker.user.repository.UserRepository;
import com.bugtracker.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    @Override
    public String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            throw new RuntimeException("No authenticated user found");
        }
        
        String username = auth.getName();
        log.debug("Current authenticated user: {}", username);
        return username;
    }

    @Override
    public Long getCurrentUserId() {
        String username = getCurrentUsername();
        
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found for username: " + username));
        
        log.debug("Current user ID: {} for username: {}", user.getId(), username);
        return user.getId();
    }

    @Override
    public boolean isCurrentUserAdmin() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            
            if (auth == null || !auth.isAuthenticated()) {
                return false;
            }
            
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
            
            log.debug("User {} has admin role: {}", auth.getName(), isAdmin);
            return isAdmin;
            
        } catch (Exception e) {
            log.warn("Error checking admin role: {}", e.getMessage());
            return false;
        }
    }
} 