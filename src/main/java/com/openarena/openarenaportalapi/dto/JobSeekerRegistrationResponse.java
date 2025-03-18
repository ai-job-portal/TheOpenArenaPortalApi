// src/main/java/com/openarena/openarenaportalapi/dto/JobSeekerRegistrationResponse.java
package com.openarena.openarenaportalapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobSeekerRegistrationResponse {
    private Integer userId; // Include the newly created user's ID!  Essential.
    private String username;
    private String email;
    private String message; // Success message
}