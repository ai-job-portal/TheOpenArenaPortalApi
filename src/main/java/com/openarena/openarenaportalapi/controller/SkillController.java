// src/main/java/com/openarena/openarenaportalapi/controller/SkillController.java
package com.openarena.openarenaportalapi.controller;

import com.openarena.openarenaportalapi.dto.SkillDTO;
import com.openarena.openarenaportalapi.service.SkillService;
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
@RequestMapping("/skills")
//@PreAuthorize("hasRole('ROLE_JARVIS')") // Secure endpoints
public class SkillController {

    private final SkillService skillService;

    @Autowired
    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping
    public ResponseEntity<SkillDTO> addSkill(@Valid @RequestBody SkillDTO skillDTO) {
        SkillDTO savedSkill = skillService.addSkill(skillDTO);
        return new ResponseEntity<>(savedSkill, HttpStatus.CREATED);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<SkillDTO>> addSkills(@Valid @RequestBody List<SkillDTO> skillDTOs) {
        List<SkillDTO> savedSkills = skillService.addSkills(skillDTOs);
        return new ResponseEntity<>(savedSkills, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SkillDTO> updateSkill(@PathVariable Integer id, @Valid @RequestBody SkillDTO skillDTO) {
        SkillDTO updatedSkill = skillService.updateSkill(id, skillDTO);
        return ResponseEntity.ok(updatedSkill);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSkill(@PathVariable Integer id) {
        skillService.deleteSkill(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping
    public ResponseEntity<Page<SkillDTO>> getAllSkills(Pageable pageable){
        return new ResponseEntity<>(skillService.getAllSkills(pageable), HttpStatus.OK);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<SkillDTO>> getRecentSkills() {
        List<SkillDTO> recentSkills = skillService.getRecentSkills();
        return ResponseEntity.ok(recentSkills);
    }

    // Search endpoint
    @GetMapping("/search")
    public ResponseEntity<Page<SkillDTO>> searchSkills(@RequestParam("query") String query, Pageable pageable) {
        Page<SkillDTO> skills = skillService.searchSkills(query, pageable);
        return ResponseEntity.ok(skills);
    }

    //For typeahead control
    @GetMapping("/find")
    public ResponseEntity<List<SkillDTO>> searchSkills(@RequestParam(name = "q") String query){
        return new ResponseEntity<>(skillService.searchSkills(query), HttpStatus.OK);
    }
}