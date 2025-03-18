package com.openarena.openarenaportalapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobSeekerRegistrationRequest {

    @NotBlank(message = "Username is required")
    @Size(max = 80, message = "Username must be less than 80 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 120, message = "Email must be less than 120 characters")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @Size(max = 100, message = "Name must be less than 100 characters")
    private String name;

    @Size(max = 20, message = "Mobile number must be less than 20 characters")
    private String mobile;
    private String skills;
    private String resumeUrl;
}