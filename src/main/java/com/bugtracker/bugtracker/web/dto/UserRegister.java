package com.bugtracker.bugtracker.web.dto;

import com.bugtracker.bugtracker.validation.PasswordMatches;
import com.bugtracker.bugtracker.validation.UniqueEmail;
import com.bugtracker.bugtracker.validation.ValidPassword;
import com.bugtracker.bugtracker.validation.groups.OnCreate;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@PasswordMatches(groups = OnCreate.class)
public class UserRegister {

    @NotBlank(message = "First name is required", groups = OnCreate.class)
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters", groups = OnCreate.class)
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s'-]+$", message = "First name can only contain letters, spaces, apostrophes and hyphens", groups = OnCreate.class)
    private String firstName;

    @NotBlank(message = "Last name is required", groups = OnCreate.class)
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters", groups = OnCreate.class)
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s'-]+$", message = "Last name can only contain letters, spaces, apostrophes and hyphens", groups = OnCreate.class)
    private String lastName;

    @NotBlank(message = "Email is required", groups = OnCreate.class)
    @Email(message = "Please provide a valid email address", groups = OnCreate.class)
    @Size(max = 100, message = "Email cannot exceed 100 characters", groups = OnCreate.class)
    @UniqueEmail(groups = OnCreate.class)
    private String email;

    @NotBlank(message = "Password is required", groups = OnCreate.class)
    @ValidPassword(groups = OnCreate.class)
    private String password;

    @NotBlank(message = "Confirm password is required", groups = OnCreate.class)
    private String confirmPassword;
} 