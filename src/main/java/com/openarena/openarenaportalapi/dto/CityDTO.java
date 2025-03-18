// src/main/java/com/openarena/openarenaportalapi/dto/CityDTO.java
package com.openarena.openarenaportalapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CityDTO {
    private Integer id;
    private String name;
    private String state;
}