package com.openarena.openarenaportalapi.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "city", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "state"}))
@Getter
@Setter
@NoArgsConstructor
@ToString
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String state;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();;
}