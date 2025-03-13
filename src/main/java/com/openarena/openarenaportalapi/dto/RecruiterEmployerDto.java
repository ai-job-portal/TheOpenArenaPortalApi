package com.openarena.openarenaportalapi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecruiterEmployerDto {
    private Long id;
    private Integer recruiterId;
    private Integer employerId;
    private String role;
}
