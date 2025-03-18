// src/main/java/com/openarena/openarenaportalapi/service/SkillServiceImpl.java
package com.openarena.openarenaportalapi.service;

import com.openarena.openarenaportalapi.exceptions.AlreadyExistException;
import com.openarena.openarenaportalapi.exceptions.NotFoundException;
import com.openarena.openarenaportalapi.model.Skill;
import com.openarena.openarenaportalapi.dto.SkillDTO;
import com.openarena.openarenaportalapi.repository.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;

    @Autowired
    public SkillServiceImpl(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    @Override
    @Transactional
    public SkillDTO addSkill(SkillDTO skillDTO) {
        if (skillRepository.existsByName(skillDTO.getName())) {
            throw new AlreadyExistException("Skill with name '" + skillDTO.getName() + "' already exists");
        }

        Skill skill = convertToEntity(skillDTO);
        skill = skillRepository.save(skill);
        return convertToDTO(skill);
    }

    @Override
    @Transactional
    public List<SkillDTO> addSkills(List<SkillDTO> skillDTOs) {
        // Check for duplicates *before* saving anything.
        for (SkillDTO dto : skillDTOs) {
            if (skillRepository.existsByName(dto.getName())) {
                throw new AlreadyExistException("Skill with name '" + dto.getName() + "' already exists");
            }
        }

        List<Skill> skills = skillDTOs.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());
        skills = skillRepository.saveAll(skills);
        return skills.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SkillDTO updateSkill(Integer id, SkillDTO skillDTO) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Skill not found with id: " + id));

        // Prevent duplicates, excluding the current skill being updated
        if (!skill.getName().equalsIgnoreCase(skillDTO.getName())) {
            if (skillRepository.existsByName(skillDTO.getName())) {
                throw new AlreadyExistException("Skill with name '" + skillDTO.getName() + "' already exists");
            }
        }

        skill.setName(skillDTO.getName());
        skill = skillRepository.save(skill);
        return convertToDTO(skill);
    }
    @Override
    @Transactional
    public void deleteSkill(Integer id) {
        if (!skillRepository.existsById(id)) {
            throw new NotFoundException("Skill not found with id: " + id);
        }
        skillRepository.deleteById(id);
    }
    @Override
    @Transactional(readOnly = true)
    public List<SkillDTO> getRecentSkills() {
        return skillRepository.findTop10ByOrderByCreatedAtDesc().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SkillDTO> getAllSkills(Pageable pageable) {
        return skillRepository.findAllByOrderByCreatedAtDesc(pageable).map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SkillDTO> searchSkills(String query, Pageable pageable) {
        return skillRepository.findByNameContainingIgnoreCase(query, pageable).map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SkillDTO> searchSkills(String query) {
        return skillRepository.findByNameContainingIgnoreCase(query).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Skill getSkillById(Integer id){
        return skillRepository.findById(id).get();
    }

    private SkillDTO convertToDTO(Skill skill) {
        return new SkillDTO(skill.getId(), skill.getName());
    }

    private Skill convertToEntity(SkillDTO skillDTO) {
        Skill skill = new Skill();
        skill.setName(skillDTO.getName());
        return skill;
    }
}