package com.bugtracker.bugtracker.web.controller;

import com.bugtracker.bugtracker.user.model.Role;
import com.bugtracker.bugtracker.user.model.User;
import com.bugtracker.bugtracker.user.repository.UserRepository;
import com.bugtracker.bugtracker.user.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class DebugController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/debug/check-user")
    @ResponseBody
    public Map<String, Object> checkUser(@RequestParam(required = false) String email) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            if (email == null || email.trim().isEmpty()) {
                // Show all users
                List<User> allUsers = userRepository.findAll();
                result.put("totalUsers", allUsers.size());
                result.put("users", allUsers.stream().map(u -> {
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("id", u.getId());
                    userInfo.put("email", u.getEmail());
                    userInfo.put("firstName", u.getFirstName());
                    userInfo.put("lastName", u.getLastName());
                    userInfo.put("isActive", u.isActive());
                    userInfo.put("roles", u.getRoles().stream().map(Role::getName).toList());
                    return userInfo;
                }).toList());
            } else {
                // Check specific user
                Optional<User> userOpt = userRepository.findByEmail(email);
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    result.put("found", true);
                    result.put("userId", user.getId());
                    result.put("email", user.getEmail());
                    result.put("firstName", user.getFirstName());
                    result.put("lastName", user.getLastName());
                    result.put("isActive", user.isActive());
                    result.put("roles", user.getRoles().stream().map(Role::getName).toList());
                    result.put("hasPassword", user.getPassword() != null && !user.getPassword().isEmpty());
                    result.put("passwordLength", user.getPassword() != null ? user.getPassword().length() : 0);
                } else {
                    result.put("found", false);
                    result.put("message", "User not found with email: " + email);
                }
            }
            
            // Show all roles
            List<Role> allRoles = roleRepository.findAll();
            result.put("availableRoles", allRoles.stream().map(Role::getName).toList());
            
        } catch (Exception e) {
            result.put("error", "Error checking user: " + e.getMessage());
        }
        
        return result;
    }
    
    @GetMapping("/debug/test-password")
    @ResponseBody
    public Map<String, Object> testPassword(@RequestParam String email, @RequestParam String password) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                boolean passwordMatches = passwordEncoder.matches(password, user.getPassword());
                
                result.put("found", true);
                result.put("email", user.getEmail());
                result.put("isActive", user.isActive());
                result.put("passwordMatches", passwordMatches);
                result.put("roles", user.getRoles().stream().map(Role::getName).toList());
                
                if (!passwordMatches) {
                    result.put("hint", "Password does not match. Make sure you're using the same password you registered with.");
                }
            } else {
                result.put("found", false);
                result.put("message", "User not found with email: " + email);
            }
        } catch (Exception e) {
            result.put("error", "Error testing password: " + e.getMessage());
        }
        
        return result;
    }
} 