package com.openarena.openarenaportalapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor // Add this constructor
public class LoginResponseDto {
    private String jwt;
}