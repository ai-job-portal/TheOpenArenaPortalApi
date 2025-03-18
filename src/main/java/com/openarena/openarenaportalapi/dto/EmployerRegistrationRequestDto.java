package com.openarena.openarenaportalapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployerRegistrationRequestDto {

    @NotBlank(message = "Company name is required")
    @Size(max = 100, message = "Company name must be less than 100 characters")
    private String companyName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 120, message = "Email must be less than 120 characters")
    private String email;

    @NotBlank(message = "Website is required")
    @Size(max = 255, message = "Website must be less than 255 characters")
    private String website;

    private String description; // Consider @Size for TEXT fields

    @NotBlank(message = "Industry is required")
    @Size(max = 100, message = "Industry must be less than 100 characters")
    private String industry;

    @NotBlank(message = "Company size is required")
    @Size(max = 50, message = "Company size must be less than 50 characters")
    private String companySize;

    @NotBlank(message = "Location is required")
    @Size(max = 100, message = "Location must be less than 100 characters")
    private String location;

    @NotBlank(message = "Admin username is required")
    @Size(max = 80, message = "Admin username must be less than 80 characters")
    private String adminUsername;

    @NotBlank(message = "Admin email is required")
    @Email(message = "Invalid email format")
    @Size(max = 120, message = "Admin email must be less than 120 characters")
    private String adminEmail;

    @NotBlank(message = "Admin password is required")
    @Size(min = 6, message = "Admin password must be at least 6 characters")
    private String adminPassword;

    @Size(max = 100, message = "Admin name must be less than 100 characters")
    private String adminName; // Optional, so no @NotBlank

    @Size(max = 20, message = "Admin mobile must be less than 20 characters")
    private String adminMobile; // Optional, so no @NotBlank
}
