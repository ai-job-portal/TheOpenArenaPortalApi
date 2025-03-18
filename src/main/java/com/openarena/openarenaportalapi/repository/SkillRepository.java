// src/main/java/com/openarena/openarenaportalapi/repository/SkillRepository.java
package com.openarena.openarenaportalapi.repository;

import com.openarena.openarenaportalapi.model.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Integer> {
    boolean existsByName(String name);

    // Find skills by name (case-insensitive)
    @Query("SELECT s FROM Skill s WHERE LOWER(s.name) LIKE LOWER(concat('%', :query, '%'))")
    List<Skill> findByNameContainingIgnoreCase(@Param("query") String query);
    @Query("SELECT s FROM Skill s WHERE LOWER(s.name) LIKE LOWER(concat('%', :query, '%'))")
    Page<Skill> findByNameContainingIgnoreCase(@Param("query") String query, Pageable pageable);


    // Find top N recently added skills
    List<Skill> findTop10ByOrderByCreatedAtDesc();

    // Find All by Order
    Page<Skill> findAllByOrderByCreatedAtDesc(Pageable pageable);
}