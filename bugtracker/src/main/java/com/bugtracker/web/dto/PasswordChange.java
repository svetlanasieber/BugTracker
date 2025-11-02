package com.bugtracker.web.dto;

import com.bugtracker.validation.PasswordMatches;
import com.bugtracker.validation.ValidPassword;
import com.bugtracker.validation.groups.OnPasswordChange;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@PasswordMatches(
    passwordField = "newPassword", 
    confirmPasswordField = "confirmPassword",
    groups = OnPasswordChange.class
)
public class PasswordChange {

    @NotBlank(message = "Current password is required", groups = OnPasswordChange.class)
    private String currentPassword;

    @NotBlank(message = "New password is required", groups = OnPasswordChange.class)
    @ValidPassword(groups = OnPasswordChange.class)
    private String newPassword;

    @NotBlank(message = "Password confirmation is required", groups = OnPasswordChange.class)
    private String confirmPassword;
} 