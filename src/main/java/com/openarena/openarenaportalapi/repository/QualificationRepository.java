// src/main/java/com/openarena/openarenaportalapi/repository/QualificationRepository.java
package com.openarena.openarenaportalapi.repository;

import com.openarena.openarenaportalapi.model.Qualification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QualificationRepository extends JpaRepository<Qualification, Integer> {
    Optional<Qualification> findByDegreeTypeAndSpecialisationAndSubtype(String degreeType, String specialisation, String subtype);
    boolean existsByDegreeTypeAndSpecialisationAndSubtype(String degreeType, String specialisation, String subtype);

    @Query("SELECT q FROM Qualification q WHERE " +
            "LOWER(q.degreeType) LIKE LOWER(concat('%', :query, '%')) OR " +
            "LOWER(q.specialisation) LIKE LOWER(concat('%', :query, '%')) OR " +
            "LOWER(q.subtype) LIKE LOWER(concat('%', :query, '%'))")
    List<Qualification> searchByDegreeTypeSpecialisationSubtype(@Param("query") String query);
    @Query("SELECT q FROM Qualification q WHERE " +
            "LOWER(q.degreeType) LIKE LOWER(concat('%', :query, '%')) OR " +
            "LOWER(q.specialisation) LIKE LOWER(concat('%', :query, '%')) OR " +
            "LOWER(q.subtype) LIKE LOWER(concat('%', :query, '%'))")
    Page<Qualification> searchByDegreeTypeSpecialisationSubtype(@Param("query") String query, Pageable pageable);


    // Find top N recently added.
    List<Qualification> findTop10ByOrderByCreatedAtDesc();
    Page<Qualification> findAllByOrderByCreatedAtDesc(Pageable pageable);

    //find list by degree type
    @Query("SELECT DISTINCT q.id, q.degreeType FROM Qualification q")
    List<Object[]> findDistinctDegreeTypes();

    //find list by degree type and specialisation
    @Query("SELECT DISTINCT q.id,q.specialisation FROM Qualification q WHERE q.degreeType = :degreeType")
    List<Object[]> findDistinctSpecialisationsByDegreeType(@Param("degreeType") String degreeType);

    //find list by degree type and specialisation and subtype
    @Query("SELECT DISTINCT q.id, q.subtype FROM Qualification q WHERE q.degreeType = :degreeType AND q.specialisation = :specialisation")
    List<Object[]> findDistinctSubtypesByDegreeTypeAndSpecialisation(@Param("degreeType") String degreeType, @Param("specialisation") String specialisation);

}