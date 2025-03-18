// src/main/java/com/openarena/openarenaportalapi/dto/EmployerRegistrationResponse.java
package com.openarena.openarenaportalapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployerRegistrationResponse {
    private Integer employerId;
    private String companyName;
    private Integer adminUserId;      // ID of the admin recruiter
    private String adminUsername;   // Username of the admin recruiter
    private String message;
}