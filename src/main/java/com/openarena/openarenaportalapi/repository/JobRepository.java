package com.openarena.openarenaportalapi.repository;


import com.openarena.openarenaportalapi.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository // Optional, but good practice
public interface JobRepository extends JpaRepository<Job, Integer> {
    // You can add custom query methods here later, if needed
    List<Job> findByIsActiveTrue();
    Optional<Job> findByIdAndIsActiveTrue(Integer id);
}