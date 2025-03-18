package com.openarena.openarenaportalapi.dto;
//src/main/java/com/example/jobportal/dto/EmployerResponseDto.java

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployerResponseDto {
    private Integer employerId;
    private String companyName;
    private Integer adminUserId;      // ID of the admin recruiter
    private String adminUsername;   // Username of the admin recruiter
    private String invitationCode;    // You might not need this in the response, depending on your flow.
    private String message;
}
