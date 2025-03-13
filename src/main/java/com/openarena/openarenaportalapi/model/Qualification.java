package com.openarena.openarenaportalapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "qualification", uniqueConstraints = @UniqueConstraint(columnNames = {"degree_type", "specialisation", "subtype"}))
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Qualification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "degree_type", nullable = false, length = 100)
    private String degreeType;

    @Column(length = 100)
    private String specialisation;

    @Column(length = 100)
    private String subtype;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();;
}