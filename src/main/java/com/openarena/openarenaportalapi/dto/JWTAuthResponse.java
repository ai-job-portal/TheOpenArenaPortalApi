// src/main/java/com/openarena/openarenaportalapi/dto/JWTAuthResponse.java
package com.openarena.openarenaportalapi.dto;

import lombok.Data;

@Data
public class JWTAuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Integer userId;
    private String username;
    private String name;
    private String role; // Add role field

    public JWTAuthResponse() {
    }
}