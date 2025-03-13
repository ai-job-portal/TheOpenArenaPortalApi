package com.openarena.openarenaportalapi.repository;


import com.openarena.openarenaportalapi.model.Recruiter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecruiterRepository extends JpaRepository<Recruiter, Integer> {
    Optional<Recruiter> findByUsername(String username);
    Optional<Recruiter> findByEmail(String email); // Find by email
    List<Recruiter> findByNameContainingIgnoreCase(String name); // Find by name (case-insensitive)
    List<Recruiter> findByEmployerId(Integer employerId); // Find all recruiters for a given employer
    List<Recruiter> findByRolesName(String roleName); //Find by role name

    boolean existsByUsername(String username); // Check if a recruiter with the username exists
    boolean existsByEmail(String email);
}