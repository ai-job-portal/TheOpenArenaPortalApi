package com.openarena.openarenaportalapi.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "employer")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Employer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "company_name", unique = true, nullable = false, length = 100)
    private String companyName;

    @Column(unique = true, nullable = false, length = 120)  //Should not be unique
    private String email;

    @Column(length = 255)
    private String website;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 100)
    private String industry;

    @Column(name = "company_size", nullable = false, length = 50)
    private String companySize;

    @Column(length = 100, nullable = false)
    private String location;

    @OneToMany(mappedBy = "employer", fetch = FetchType.LAZY, cascade = CascadeType.ALL) // Cascade operations
    @ToString.Exclude
    private Set<Job> jobs = new HashSet<>();

    @OneToMany(mappedBy = "employer")
    @ToString.Exclude // Prevent circular references
    private Set<Invitation> invitations;

    @OneToMany(mappedBy = "employer")
    @ToString.Exclude // Prevent circular references
    private Set<DeletedRecruiter> deletedRecruiters;

    // Corrected: One-to-many relationship with RecruiterEmployer
    @OneToMany(mappedBy = "employer", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<RecruiterEmployer> recruiterEmployers = new HashSet<>();
}