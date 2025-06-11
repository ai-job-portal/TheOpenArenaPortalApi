// src/main/java/com/openarena/openarenaportalapi/service/QualificationServiceImpl.java
package com.openarena.openarenaportalapi.service;

import com.openarena.openarenaportalapi.exceptions.AlreadyExistException;
import com.openarena.openarenaportalapi.exceptions.NotFoundException;
import com.openarena.openarenaportalapi.model.Qualification;
import com.openarena.openarenaportalapi.dto.QualificationDTO;
import com.openarena.openarenaportalapi.repository.QualificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QualificationServiceImpl implements QualificationService {

    private final QualificationRepository qualificationRepository;

    @Autowired
    public QualificationServiceImpl(QualificationRepository qualificationRepository) {
        this.qualificationRepository = qualificationRepository;
    }

    @Override
    @Transactional
    public QualificationDTO addQualification(QualificationDTO qualificationDTO) {
        if (qualificationRepository.existsByDegreeTypeAndSpecialisationAndSubtype(
                qualificationDTO.getDegreeType(), qualificationDTO.getSpecialisation(), qualificationDTO.getSubtype())) {
            throw new AlreadyExistException("Qualification already exists");
        }

        Qualification qualification = convertToEntity(qualificationDTO);
        qualification = qualificationRepository.save(qualification);
        return convertToDTO(qualification);
    }

    @Override
    @Transactional
    public List<QualificationDTO> addQualifications(List<QualificationDTO> qualificationDTOs) {
        // Efficient duplicate checking (avoid N+1 problem)
        for (QualificationDTO dto : qualificationDTOs) {
            if (qualificationRepository.existsByDegreeTypeAndSpecialisationAndSubtype(
                    dto.getDegreeType(), dto.getSpecialisation(), dto.getSubtype())) {
                throw new AlreadyExistException("Qualification already exists: " +
                        dto.getDegreeType() + " - " +
                        (dto.getSpecialisation() != null ? dto.getSpecialisation() + " - " : "") +
                        (dto.getSubtype() != null ? dto.getSubtype() : ""));
            }
        }

        List<Qualification> qualifications = qualificationDTOs.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());
        qualifications = qualificationRepository.saveAll(qualifications);
        return qualifications.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public QualificationDTO updateQualification(Integer id, QualificationDTO qualificationDTO) {
        Qualification qualification = qualificationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Qualification not found with id: " + id));

        // Prevent duplicates, excluding the current qualification being updated.
        if (!qualification.getDegreeType().equalsIgnoreCase(qualificationDTO.getDegreeType())
                || !qualification.getSpecialisation().equalsIgnoreCase(qualificationDTO.getSpecialisation())
                || !qualification.getSubtype().equalsIgnoreCase(qualificationDTO.getSubtype())) {
            if (qualificationRepository.existsByDegreeTypeAndSpecialisationAndSubtype(
                    qualificationDTO.getDegreeType(), qualificationDTO.getSpecialisation(), qualificationDTO.getSubtype()
            )) {
                throw new AlreadyExistException("Qualification already exists");
            }
        }

        qualification.setDegreeType(qualificationDTO.getDegreeType());
        qualification.setSpecialisation(qualificationDTO.getSpecialisation());
        qualification.setSubtype(qualificationDTO.getSubtype());
        qualification = qualificationRepository.save(qualification);
        return convertToDTO(qualification);
    }

    @Override
    @Transactional
    public void deleteQualification(Integer id) {
        if (!qualificationRepository.existsById(id)) {
            throw new NotFoundException("Qualification not found with id: " + id);
        }
        qualificationRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QualificationDTO> getRecentQualifications() {
        return qualificationRepository.findTop10ByOrderByCreatedAtDesc().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QualificationDTO> getAllQualifications(Pageable pageable) {
        return qualificationRepository.findAllByOrderByCreatedAtDesc(pageable).map(this::convertToDTO);
    }
    @Override
    @Transactional(readOnly = true)
    public Page<QualificationDTO> searchQualifications(String query, Pageable pageable) {
        return qualificationRepository.searchByDegreeTypeSpecialisationSubtype(query, pageable).map(this::convertToDTO);
    }

    @Override
    public List<QualificationDTO> searchQualifications(String query) {
        return qualificationRepository.searchByDegreeTypeSpecialisationSubtype(query).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Qualification getQualificationById(Integer id) {
        return qualificationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Qualification not found with id: " + id));
    }

    private QualificationDTO convertToDTO(Qualification qualification) {
        return new QualificationDTO(qualification.getId(), qualification.getDegreeType(),
                qualification.getSpecialisation(), qualification.getSubtype());
    }

    private Qualification convertToEntity(QualificationDTO qualificationDTO) {
        Qualification qualification = new Qualification();
        qualification.setDegreeType(qualificationDTO.getDegreeType());
        qualification.setSpecialisation(qualificationDTO.getSpecialisation());
        qualification.setSubtype(qualificationDTO.getSubtype());
        return qualification;
    }

    @Override
    public List<Object[]> getDegreeTypes(){
        return qualificationRepository.findDistinctDegreeTypes();
    }

    @Override
    public List<Object[]> getSpecialization(String degreeType){
        return qualificationRepository.findDistinctSpecialisationsByDegreeType(degreeType);
    }

    @Override
    public List<Object[]> getSubtype(String degreeType, String specialisation){
        return qualificationRepository.findDistinctSubtypesByDegreeTypeAndSpecialisation(degreeType, specialisation);
    }
}