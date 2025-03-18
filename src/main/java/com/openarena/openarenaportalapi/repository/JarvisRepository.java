// src/main/java/com/openarena/openarenaportalapi/repository/JarvisRepository.java
package com.openarena.openarenaportalapi.repository;

import com.openarena.openarenaportalapi.model.Jarvis;
import com.openarena.openarenaportalapi.model.JobSeeker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JarvisRepository extends JpaRepository<Jarvis, Long> { // Use Long
    Optional<Jarvis> findByUsername(String username);
    Optional<Jarvis> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}