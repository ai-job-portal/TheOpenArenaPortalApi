package com.openarena.openarenaportalapi.repository;



import com.openarena.openarenaportalapi.model.Employer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployerRepository extends JpaRepository<Employer, Integer> {
    boolean existsByEmail(String email);
}