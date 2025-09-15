package com.bugtracker.bugtracker.user.service;

import com.bugtracker.bugtracker.web.dto.PasswordChange;
import com.bugtracker.bugtracker.web.dto.ProfileEdit;
import com.bugtracker.bugtracker.web.dto.UserRegister;
import com.bugtracker.bugtracker.user.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserService {
    User registerUser(UserRegister userRegister);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    List<User> findAllUsers();
    void changeUserRole(Long userId, String roleName);
    boolean existsByEmail(String email);
    
  
    User findByUsername(String username);
    

    List<User> findAll();
    
  
    User updateUser(User user);
    
 
    User updateUserProfile(String username, ProfileEdit profileEdit);
    boolean changeUserPassword(String username, PasswordChange passwordChange);
    User getCurrentUserWithStats(String username);

    String resetAdminUser();

    Map<String, Object> getSystemDebugInfo();

    boolean isUserAdmin(String username);

    User createUserWithRoles(String firstName, String lastName, String email, String password, List<String> roleNames);
} 
