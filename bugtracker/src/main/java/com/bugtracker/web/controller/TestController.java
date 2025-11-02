package com.bugtracker.web.controller;

import com.bugtracker.user.model.Role;
import com.bugtracker.user.model.User;
import com.bugtracker.user.repository.RoleRepository;
import com.bugtracker.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/users")
    @ResponseBody
    public Map<String, Object> getAllUsers() {
        Map<String, Object> response = new HashMap<>();
        List<User> users = userRepository.findAll();
        List<Role> roles = roleRepository.findAll();
        response.put("totalUsers", users.size());
        response.put("totalRoles", roles.size());
        response.put("users", users.stream().map(user -> {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("email", user.getEmail());
            userInfo.put("firstName", user.getFirstName());
            userInfo.put("lastName", user.getLastName());
            userInfo.put("isActive", user.isActive());
            userInfo.put("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
            userInfo.put("hasPassword", user.getPassword() != null && !user.getPassword().isEmpty());
            return userInfo;
        }).collect(Collectors.toList()));
        response.put("roles", roles.stream().map(role -> {
            Map<String, Object> roleInfo = new HashMap<>();
            roleInfo.put("id", role.getId());
            roleInfo.put("name", role.getName());
            return roleInfo;
        }).collect(Collectors.toList()));
        return response;
    }

    @GetMapping("/check-admin")
    @ResponseBody
    public Map<String, Object> checkAdmin() {
        Map<String, Object> response = new HashMap<>();
        var adminOpt = userRepository.findByEmail("admin@bugtracker.com");
        if (adminOpt.isPresent()) {
            User admin = adminOpt.get();
            response.put("found", true);
            response.put("email", admin.getEmail());
            response.put("isActive", admin.isActive());
            response.put("roles", admin.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
            boolean passwordMatches = passwordEncoder.matches("Admin123!", admin.getPassword());
            response.put("passwordMatches", passwordMatches);
            response.put("encodedPassword", admin.getPassword());
        } else {
            response.put("found", false);
            response.put("message", "Admin user not found");
        }
        return response;
    }
} 