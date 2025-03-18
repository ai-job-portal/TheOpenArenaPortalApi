// src/main/java/com/openarena/openarenaportalapi/repository/CityRepository.java
package com.openarena.openarenaportalapi.repository;

import com.openarena.openarenaportalapi.model.City;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Integer> {
    Optional<City> findByNameAndState(String name, String state);
    boolean existsByNameAndState(String name, String state);

    // Search by name or state (case-insensitive)
    @Query("SELECT c FROM City c WHERE LOWER(c.name) LIKE LOWER(concat('%', :query, '%')) OR LOWER(c.state) LIKE LOWER(concat('%', :query, '%'))")
    List<City> searchByNameOrState(@Param("query") String query);

    @Query("SELECT c FROM City c WHERE LOWER(c.name) LIKE LOWER(concat('%', :query, '%')) OR LOWER(c.state) LIKE LOWER(concat('%', :query, '%'))")
    Page<City> searchByNameOrState(@Param("query") String query, Pageable pageable);

    // Find top N recently added.
    List<City> findTop10ByOrderByCreatedAtDesc();
    Page<City> findAllByOrderByCreatedAtDesc(Pageable pageable);
}