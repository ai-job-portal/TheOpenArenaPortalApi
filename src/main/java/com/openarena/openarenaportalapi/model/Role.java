package com.openarena.openarenaportalapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name; // e.g., "ROLE_JOBSEEKER", "ROLE_RECRUITER", "ROLE_EMPLOYER", "ROLE_JARVIS"

    public Role(String name) {
        this.name = name;
    }
}