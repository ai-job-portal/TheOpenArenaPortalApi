// src/main/java/com/openarena/openarenaportalapi/dto/EmployerLoginRequest.java
package com.openarena.openarenaportalapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployerLoginRequest {
    @NotBlank(message = "Username or email is required") // Better message
    private String usernameOrEmail;  // Changed field name

    @NotBlank(message = "Password is required")
    private String password;
    private String userType = "employer";
}