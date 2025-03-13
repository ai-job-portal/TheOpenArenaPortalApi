package com.openarena.openarenaportalapi.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateJobDto {

    @NotBlank(message = "Title is required") // Validation
    @Size(max = 100, message = "Title must be less than 100 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 65535, message = "Description must be less than 65535 characters") // For TEXT type
    private String description;

    @NotBlank(message = "Skills are required")
    @Size(max=500, message = "Skills must be less than 500 characters")
    private String skills;

    private String location;

    @Column(name = "salary_range")
    private String salaryRange;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "experience_level")
    private String experienceLevel;

    @Column(name = "workplace_type")
    private String workplaceType;

    @Column(name = "employment_type")
    private String employmentType;

    @Column(name = "educational_qualifications")
    private String educationalQualifications;

    @Column(name = "about_company")
    private String aboutCompany;

    @Column(name = "post_date")
    private LocalDate postDate;

    @Column(name = "total_openings")
    private Integer totalOpenings;

}