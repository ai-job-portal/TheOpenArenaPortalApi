package com.openarena.openarenaportalapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class JobDto {

    private Integer id; // Use Long for consistency with your entity's ID type
    private String title;
    private String description;
    private String skills;
    private String location;
    private String salaryRange;
    private String companyName;
    private String experienceLevel;
    private String workplaceType;
    private String employmentType;
    private String educationalQualifications;
    private String aboutCompany;
    private LocalDateTime postDate; // Or LocalDateTime, depending on your Job entity
    private Integer totalOpenings;
    private Integer employerId;  // Include employerId
    private String employerName;
    private Integer postedBy; // Include postedBy (recruiter ID)
    private String posterName;
    private Boolean isActive; // Include isActive

}