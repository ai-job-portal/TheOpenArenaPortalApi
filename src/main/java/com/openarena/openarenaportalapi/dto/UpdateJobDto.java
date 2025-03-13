package com.openarena.openarenaportalapi.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateJobDto {

    @Size(max = 100, message = "Title must be less than 100 characters")
    private String title;

    @Size(max = 65535, message = "Description must be less than 65535 characters") // For TEXT type
    private String description;

    @Size(max = 500, message = "Skills must be less than 500 characters")
    private String skills;

    private String location;
    private String salaryRange;
    private String companyName;
    private String experienceLevel;
    private String workplaceType;
    private String employmentType;
    private String educationalQualifications;
    private String aboutCompany;
    private LocalDate postDate;
    private Integer totalOpenings;
    // Note: We generally *don't* allow updating employerId or postedBy via a regular update.
    // Those are typically set at creation and shouldn't change.
    private Boolean isActive;

}