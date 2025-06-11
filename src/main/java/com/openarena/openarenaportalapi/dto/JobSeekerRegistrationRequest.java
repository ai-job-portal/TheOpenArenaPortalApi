package com.openarena.openarenaportalapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data; // Using @Data for getters, setters, toString, etc.
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data // Includes @Getter, @Setter, @ToString, @EqualsAndHashCode, @RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
public class JobSeekerRegistrationRequest {

    // Fields from personalInfo in Angular form
    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name must be less than 100 characters")
    private String fullName; // Was 'name' in DTO, mapping from Angular's personalInfo.fullName

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 120, message = "Email must be less than 120 characters")
    private String email; // From Angular's personalInfo.email

    @NotBlank(message = "Phone number is required")
    @Size(max = 20, message = "Phone number must be less than 20 characters") // Example +911234567890 is 13 chars
    private String phone; // From Angular's personalInfo.phone (will include +91)

    @NotBlank(message = "City is required")
    @Size(max = 100)
    private String locationCity; // From Angular's personalInfo.locationCity

    @NotBlank(message = "Country is required")
    @Size(max = 100)
    private String locationCountry; // From Angular's personalInfo.locationCountry (disabled, defaults to India)

    // Fields from credentials in Angular form
    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 80, message = "Username must be between 4 and 80 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    // Top-level fields from Angular form (if applicable)
    // The DTO only had 'skills', 'resumeUrl' previously here
    // 'skills' might be sent from 'professionalInfo' in a flat way
    @Size(max = 500, message = "Skills must be less than 500 characters")
    private String keySkills; // From Angular's professionalInfo.keySkills, sent as comma-separated

    // 'resumeUrl' is removed as it will be handled by file upload
    // No 'receivePromotions' field in this DTO, add if needed on backend

    // Note: I've made assumptions on how Angular's nested form groups
    // will map to this flat DTO. Angular will need to send FormData keys
    // that match these DTO field names for automatic binding with @ModelAttribute.
    // E.g., Angular sends 'fullName' key, not 'personalInfo.fullName'
}