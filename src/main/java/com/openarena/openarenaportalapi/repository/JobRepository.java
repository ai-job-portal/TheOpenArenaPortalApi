package com.openarena.openarenaportalapi.repository;

import com.openarena.openarenaportalapi.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, Integer> {

    List<Job> findByIsActiveTrue();
    Optional<Job> findByIdAndIsActiveTrue(Integer id);
    List<Job> findByEmployerId(Integer employerId);

    @Query("SELECT j FROM Job j WHERE j.employerId = :employerId AND j.isActive = true")
    List<Job> findActiveJobsByEmployerId(@Param("employerId") Integer employerId);

    // Find jobs expiring within a certain date range for a specific employer
    @Query("SELECT j FROM Job j WHERE j.employerId = :employerId AND j.expiryDate BETWEEN :startDate AND :endDate")
    List<Job> findExpiringJobs(@Param("employerId") Integer employerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT j FROM Job j WHERE j.employerId = :employerId AND j.postDate >= :startDate")
    List<Job> findRecentJobs(@Param("employerId") Integer employerId, @Param("startDate") LocalDateTime startDate);

    List<Job> findByEmployerIdAndIsActiveTrue(Integer employerId);

    @Query("SELECT j from Job j WHERE j.expiryDate < :now")
    List<Job> findExpiredJobs(@Param("now") LocalDateTime now);
}