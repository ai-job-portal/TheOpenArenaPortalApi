package com.openarena.openarenaportalapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "invitation")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Invitation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "employer_id", nullable = false)
    private Integer employerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id", referencedColumnName = "id",insertable = false, updatable = false)
    @ToString.Exclude
    private Employer employer;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private Boolean used = false; //Default Value

    @Column(length = 50)
    private String role = "recruiter"; //Default value

    @Column(length = 120)
    private String email;
}