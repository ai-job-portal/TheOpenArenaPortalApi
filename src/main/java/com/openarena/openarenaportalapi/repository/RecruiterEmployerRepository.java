package com.openarena.openarenaportalapi.repository;



import com.openarena.openarenaportalapi.model.RecruiterEmployer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecruiterEmployerRepository extends JpaRepository<RecruiterEmployer, Long> {
    List<RecruiterEmployer> findByRecruiterId(Integer recruiterId);
    Optional<RecruiterEmployer> findByRecruiterIdAndEmployerId(Integer recruiterId, Integer employerId);
}
