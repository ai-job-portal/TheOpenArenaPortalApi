package com.openarena.openarenaportalapi.repository;



import com.openarena.openarenaportalapi.model.JobSeeker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobSeekerRepository extends JpaRepository<JobSeeker, Long> {
    Optional<JobSeeker> findByUsername(String username);
    Optional<JobSeeker> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}