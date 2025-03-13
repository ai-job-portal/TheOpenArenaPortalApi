package com.openarena.openarenaportalapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "deletedrecruiter")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class DeletedRecruiter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false, length = 80)
    private String username;

    @Column(unique = true, nullable = false, length = 120)
    private String email;

    @Column(nullable = false, length = 128)
    private String password;

    @Column(length = 100)
    private String name;

    @Column(length = 20)
    private String mobile;

    @Column(name = "employer_id", nullable = false)
    private Integer employerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ToString.Exclude
    private Employer employer;

    @Column(nullable = false, length = 50)
    private String role;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt = LocalDateTime.now();;
}