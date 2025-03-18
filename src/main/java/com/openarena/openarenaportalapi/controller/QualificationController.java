// src/main/java/com/openarena/openarenaportalapi/controller/QualificationController.java
package com.openarena.openarenaportalapi.controller;

import com.openarena.openarenaportalapi.dto.QualificationDTO;
import com.openarena.openarenaportalapi.service.QualificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/qualifications")
@PreAuthorize("hasRole('ROLE_JARVIS')")
public class QualificationController {

    private final QualificationService qualificationService;

    @Autowired
    public QualificationController(QualificationService qualificationService) {
        this.qualificationService = qualificationService;
    }

    @PostMapping
    public ResponseEntity<QualificationDTO> addQualification(@Valid @RequestBody QualificationDTO qualificationDTO) {
        QualificationDTO savedQualification = qualificationService.addQualification(qualificationDTO);
        return new ResponseEntity<>(savedQualification, HttpStatus.CREATED);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<QualificationDTO>> addQualifications(@Valid @RequestBody List<QualificationDTO> qualificationDTOs) {
        List<QualificationDTO> savedQualifications = qualificationService.addQualifications(qualificationDTOs);
        return new ResponseEntity<>(savedQualifications, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<QualificationDTO> updateQualification(@PathVariable Integer id, @Valid @RequestBody QualificationDTO qualificationDTO) {
        QualificationDTO updatedQualification = qualificationService.updateQualification(id, qualificationDTO);
        return ResponseEntity.ok(updatedQualification);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQualification(@PathVariable Integer id) {
        qualificationService.deleteQualification(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/recent")
    public ResponseEntity<List<QualificationDTO>> getRecentQualifications() {
        List<QualificationDTO> recentQualifications = qualificationService.getRecentQualifications();
        return ResponseEntity.ok(recentQualifications);
    }

    @GetMapping
    public ResponseEntity<Page<QualificationDTO>> getAllQualifications(Pageable pageable) {
        Page<QualificationDTO> qualificationPage = qualificationService.getAllQualifications(pageable);
        return ResponseEntity.ok(qualificationPage);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<QualificationDTO>> searchQualifications(@RequestParam("query") String query, Pageable pageable) {
        Page<QualificationDTO> qualifications = qualificationService.searchQualifications(query, pageable);
        return ResponseEntity.ok(qualifications);
    }

    //For typeahead control
    @GetMapping("/typeahead")
    public ResponseEntity<List<QualificationDTO>> searchQualification(@RequestParam(name = "q") String query){
        return new ResponseEntity<>(qualificationService.searchQualifications(query), HttpStatus.OK);
    }

    @GetMapping("/degree-types")
    public ResponseEntity<List<String>> getDegreeTypes() {
        List<String> degreeTypes = qualificationService.getDegreeTypes();
        return ResponseEntity.ok(degreeTypes);
    }

    @GetMapping("/specializations")
    public ResponseEntity<List<String>> getSpecializations(@RequestParam("degreeType") String degreeType) {
        List<String> specializations = qualificationService.getSpecialization(degreeType);
        return ResponseEntity.ok(specializations);
    }

    @GetMapping("/subtypes")
    public  ResponseEntity<List<String>> getSubTypes(@RequestParam("degreeType") String degreeType, @RequestParam("specialisation") String specialisation){
        List<String> subtypes = qualificationService.getSubtype(degreeType, specialisation);
        return ResponseEntity.ok(subtypes);
    }
}