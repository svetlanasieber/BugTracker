package com.bugtracker.web.dto;

import com.bugtracker.validation.ValidFileUpload;
import com.bugtracker.validation.groups.OnUpdate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileEdit {

    @NotBlank(message = "First name is required", groups = OnUpdate.class)
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters", groups = OnUpdate.class)
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s'-]+$", message = "First name can only contain letters, spaces, apostrophes and hyphens", groups = OnUpdate.class)
    private String firstName;

    @NotBlank(message = "Last name is required", groups = OnUpdate.class)
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters", groups = OnUpdate.class)
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s'-]+$", message = "Last name can only contain letters, spaces, apostrophes and hyphens", groups = OnUpdate.class)
    private String lastName;
    
    @ValidFileUpload(
        maxSize = 5242880, 
        allowedTypes = {"image/jpeg", "image/png", "image/gif"},
        required = false,
        message = "Profile image must be JPEG, PNG or GIF and under 5MB",
        groups = OnUpdate.class
    )
    private MultipartFile profileImage;
} 