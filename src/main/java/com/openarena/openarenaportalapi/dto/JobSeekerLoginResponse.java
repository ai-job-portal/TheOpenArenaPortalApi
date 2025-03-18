package com.openarena.openarenaportalapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobSeekerLoginResponse {

    private Long userId; // Include the user's ID. VERY important.
    private String username; // Good practice to return username/email
    //  private String email; //Consider if you want to include the email
    private String name;
}