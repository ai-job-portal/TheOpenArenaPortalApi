// src/main/java/com/openarena/openarenaportalapi/service/QualificationService.java
package com.openarena.openarenaportalapi.service;

import com.openarena.openarenaportalapi.model.Qualification;
import com.openarena.openarenaportalapi.dto.QualificationDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface QualificationService {
    QualificationDTO addQualification(QualificationDTO qualificationDTO);
    List<QualificationDTO> addQualifications(List<QualificationDTO> qualificationDTOs);
    QualificationDTO updateQualification(Integer id, QualificationDTO qualificationDTO);
    void deleteQualification(Integer id);
    List<QualificationDTO> getRecentQualifications();

    List<String> getDegreeTypes();

    List<String> getSpecialization(String degreeType);

    List<String> getSubtype(String degreeType, String specialisation);
    Page<QualificationDTO> getAllQualifications(Pageable pageable);
    Page<QualificationDTO> searchQualifications(String query, Pageable pageable);
    List<QualificationDTO> searchQualifications(String query);
    Qualification getQualificationById(Integer id);
}