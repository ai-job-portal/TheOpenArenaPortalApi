// src/main/java/com/openarena/openarenaportalapi/dto/QualificationDTO.java
package com.openarena.openarenaportalapi.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QualificationDTO {
    private Integer id;
    private String degreeType;
    private String specialisation;  // Can be null
    private String subtype;      // Can be null
}