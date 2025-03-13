package com.openarena.openarenaportalapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "job")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(name = "company_name",nullable = false, length = 100)
    private String companyName;

    @Column(name = "experience_level", nullable = false, length = 20)
    private String experienceLevel;

    @Column(name = "salary_range", nullable = false, length = 50)
    private String salaryRange;

    @Column(name = "workplace_type", nullable = false, length = 20)
    private String workplaceType;

    @Column(name = "employment_type", nullable = false, length = 20)
    private String employmentType;

    @Column(nullable = false)
    private String location;

    @Column(name = "educational_qualifications", length = 100)
    private String educationalQualifications;

    @Column(nullable = false, length = 500)
    private String skills;

    @Column(name = "about_company", columnDefinition = "TEXT")
    private String aboutCompany;

    @Column(name = "post_date", nullable = false)
    private LocalDateTime postDate;

    @Column(name = "total_openings")
    private Integer totalOpenings;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "employer_id", nullable = false)
    private Integer employerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ToString.Exclude
    private Employer employer;

    @Column(name = "posted_by", nullable = false)
    private Integer postedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "posted_by", referencedColumnName = "id", insertable = false, updatable = false)
    @ToString.Exclude
    private Recruiter poster;


    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;  //Default Value

    @OneToMany(mappedBy = "job", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<JobShare> shares = new HashSet<>();
}