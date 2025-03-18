// src/main/java/com/openarena/openarenaportalapi/service/SkillService.java
package com.openarena.openarenaportalapi.service;

import com.openarena.openarenaportalapi.dto.SkillDTO;
import com.openarena.openarenaportalapi.model.Skill;
import com.openarena.openarenaportalapi.dto.SkillDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SkillService {
    SkillDTO addSkill(SkillDTO skillDTO);
    List<SkillDTO> addSkills(List<SkillDTO> skillDTOs);
    SkillDTO updateSkill(Integer id, SkillDTO skillDTO);
    void deleteSkill(Integer id);
    List<SkillDTO> getRecentSkills();
    Page<SkillDTO> getAllSkills(Pageable pageable);
    Page<SkillDTO> searchSkills(String query, Pageable pageable);
    List<SkillDTO> searchSkills(String query);
    Skill getSkillById(Integer id);
}