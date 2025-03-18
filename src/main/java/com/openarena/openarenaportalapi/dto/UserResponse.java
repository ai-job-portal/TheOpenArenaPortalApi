// src/main/java/com/openarena/openarenaportalapi/model/dto/UserResponse.java
package com.openarena.openarenaportalapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Integer userId;
    private String username;
    private String name;
    private String role;
}