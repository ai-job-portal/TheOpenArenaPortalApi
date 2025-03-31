// src/main/java/com/openarena/openarenaportalapi/dto/JarvisLoginRequest.java
package com.openarena.openarenaportalapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JarvisLoginRequest {
    @NotBlank(message = "Username or email is required")
    private String usernameOrEmail;

    @NotBlank(message = "Password is required")
    private String password;
    private String userType = "jarvis";
}