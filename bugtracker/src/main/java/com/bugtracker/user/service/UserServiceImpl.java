package com.bugtracker.user.service;

import com.bugtracker.web.dto.PasswordChange;
import com.bugtracker.web.dto.ProfileEdit;
import com.bugtracker.web.dto.UserRegister;
import com.bugtracker.user.model.Role;
import com.bugtracker.user.model.User;
import com.bugtracker.user.repository.RoleRepository;
import com.bugtracker.user.repository.UserRepository;
import com.bugtracker.config.FileStorageProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import com.bugtracker.exception.FileUploadException;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageProperties fileStorageProperties;

    @Override
    @Transactional
    public User registerUser(UserRegister userRegister) {
        User newUser = User.builder()
                .firstName(userRegister.getFirstName())
                .lastName(userRegister.getLastName())
                .username(userRegister.getUsername())
                .email(userRegister.getEmail())
                .team(userRegister.getTeam())
                .password(passwordEncoder.encode(userRegister.getPassword()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isActive(true)
                .roles(new HashSet<>())
                .build();

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Default USER role not found"));
        newUser.getRoles().add(userRole);

        return userRepository.save(newUser);
    }

    @Override
    @Transactional
    public User createUser(UserRegister userRegister) {
        return registerUser(userRegister);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void changeUserRole(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        final String roleNameWithPrefix;
        if (!roleName.startsWith("ROLE_")) {
            roleNameWithPrefix = "ROLE_" + roleName;
        } else {
            roleNameWithPrefix = roleName;
        }
        Role role = roleRepository.findByName(roleNameWithPrefix)
                .orElseThrow(() -> new RuntimeException("Role not found with name: " + roleNameWithPrefix));

        boolean hasRole = user.getRoles().stream()
                .anyMatch(r -> r.getName().equals(roleNameWithPrefix));

        if (hasRole) {
            user.getRoles().removeIf(r -> r.getName().equals(roleNameWithPrefix));
        } else {
            user.getRoles().add(role);
        }

        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User findByUsername(String username) {
        return findByEmail(username)
            .orElseThrow(() -> new RuntimeException("User not found with email: " + username));
    }

    @Override
    public List<User> findAll() {
        return findAllUsers();
    }
    @Override
    @Transactional
    public User updateUser(User user) {
        if (!userRepository.existsById(user.getId())) {
            throw new RuntimeException("User not found with id: " + user.getId());
        }
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
    @Override
    @Transactional
    public User updateUserProfile(String username, ProfileEdit profileEdit) {
        User currentUser = findByUsername(username);
        currentUser.setFirstName(profileEdit.getFirstName());
        currentUser.setLastName(profileEdit.getLastName());
        currentUser.setUpdatedAt(LocalDateTime.now());
        MultipartFile profileImage = profileEdit.getProfileImage();
        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                String imagePath = saveProfileImage(profileImage);
                currentUser.setProfileImage(imagePath);
            } catch (IOException e) {
                throw new FileUploadException("Failed to upload profile image: " + e.getMessage(), e);
            }
        }
        log.info("Updating profile for user: {}", username);
        return updateUser(currentUser);
    }
    @Override
    @Transactional
    public boolean changeUserPassword(String username, PasswordChange passwordChange) {
        User currentUser = findByUsername(username);
        if (!passwordEncoder.matches(passwordChange.getCurrentPassword(), currentUser.getPassword())) {
            log.warn("Password change failed for user {}: incorrect current password", username);
            return false;
        }
        currentUser.setPassword(passwordEncoder.encode(passwordChange.getNewPassword()));
        currentUser.setUpdatedAt(LocalDateTime.now());
        updateUser(currentUser);
        log.info("Password changed successfully for user: {}", username);
        return true;
    }
    
    @Override
    public User getCurrentUserWithStats(String username) {
        User currentUser = findByUsername(username);
        return currentUser;
    }
    
    private String saveProfileImage(MultipartFile profileImage) throws IOException {
        Path profilesPath = Paths.get(fileStorageProperties.uploadDir(), "profiles");
        if (!Files.exists(profilesPath)) {
            Files.createDirectories(profilesPath);
        }
        String fileExtension = getFileExtension(profileImage.getOriginalFilename());
        if (!fileStorageProperties.isExtensionAllowed(fileExtension)) {
            throw new FileUploadException("File extension not allowed: " + fileExtension);
        }
        if (profileImage.getSize() > fileStorageProperties.maxFileSize()) {
            throw new FileUploadException("File size exceeds maximum allowed size of " + fileStorageProperties.maxFileSize() + " bytes");
        }
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
        Path filePath = profilesPath.resolve(uniqueFileName);
        Files.copy(profileImage.getInputStream(), filePath);
        return "/uploads/profiles/" + uniqueFileName;
    }
    
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
    @Override
    @Transactional
    public String resetAdminUser() {
        try {
            User adminUser = userRepository.findByEmail("admin@example.com")
                    .orElse(null);
            if (adminUser == null) {
                adminUser = User.builder()
                        .firstName("Admin")
                        .lastName("User")
                        .email("admin@example.com")
                        .password(passwordEncoder.encode("admin"))
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .isActive(true)
                        .roles(new HashSet<>())
                        .build();
                log.info("Creating new admin user");
            } else {
                adminUser.setPassword(passwordEncoder.encode("admin"));
                adminUser.setUpdatedAt(LocalDateTime.now());
                adminUser.setActive(true);
                log.info("Resetting existing admin user");
            }
            Role userRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new RuntimeException("USER role not found"));
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new RuntimeException("ADMIN role not found"));
            adminUser.getRoles().add(userRole);
            adminUser.getRoles().add(adminRole);
            userRepository.save(adminUser);
            return "Admin account reset successful. Email: admin@example.com, Password: admin";
        } catch (Exception e) {
            log.error("Error resetting admin user: {}", e.getMessage(), e);
            return "Error resetting admin user: " + e.getMessage();
        }
    }
    @Override
    public Map<String, Object> getSystemDebugInfo() {
        Map<String, Object> debugInfo = new HashMap<>();
        try {
            debugInfo.put("userCount", userRepository.count());
            List<Role> roles = roleRepository.findAll();
            List<String> roleNames = roles.stream()
                .map(Role::getName)
                .collect(Collectors.toList());
            debugInfo.put("roles", roleNames);
            List<User> allUsers = userRepository.findAll();
            List<User> adminUsers = allUsers.stream()
                .filter(u -> u.getRoles().stream()
                    .anyMatch(r -> "ADMIN".equals(r.getName())))
                .collect(Collectors.toList());
            List<Map<String, Object>> adminDetails = adminUsers.stream().map(u -> {
                Map<String, Object> details = new HashMap<>();
                details.put("id", u.getId());
                details.put("email", u.getEmail());
                details.put("isActive", u.isActive());
                return details;
            }).collect(Collectors.toList());
            debugInfo.put("adminUsers", adminDetails);
            log.info("System debug info requested");
        } catch (Exception e) {
            log.error("Error getting system debug info: {}", e.getMessage(), e);
            debugInfo.put("error", "Error retrieving debug information: " + e.getMessage());
        }
        return debugInfo;
    }
    
    @Override
    public boolean isUserAdmin(String username) {
        try {
            User user = findByUsername(username);
            return user.getRoles().stream()
                    .anyMatch(role -> "ROLE_ADMIN".equals(role.getName()));
        } catch (Exception e) {
            log.warn("Error checking admin status for user {}: {}", username, e.getMessage());
            return false;
        }
    }
    
    @Override
    @Transactional
    public User createUserWithRoles(String firstName, String lastName, String email, String password, List<String> roleNames) {
        log.info("Admin creating user: {} with roles: {}", email, roleNames);
        if (existsByEmail(email)) {
            throw new RuntimeException("User with email " + email + " already exists");
        }
        User newUser = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password(passwordEncoder.encode(password))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isActive(true)
                .roles(new HashSet<>())
                .build();
        if (roleNames != null && !roleNames.isEmpty()) {
            for (String roleName : roleNames) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
                newUser.getRoles().add(role);
            }
        } else {
            Role userRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new RuntimeException("Default USER role not found"));
            newUser.getRoles().add(userRole);
        }
        User savedUser = userRepository.save(newUser);
        log.info("Successfully created user: {} with roles: {}", email, 
                savedUser.getRoles().stream().map(Role::getName).collect(java.util.stream.Collectors.toList()));
        return savedUser;
    }

} 
