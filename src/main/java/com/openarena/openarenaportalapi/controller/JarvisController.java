// src/main/java/com/openarena/openarenaportalapi/controller/JarvisController.java
package com.openarena.openarenaportalapi.controller;

import com.openarena.openarenaportalapi.exceptions.DuplicateResourceException;
import com.openarena.openarenaportalapi.model.Jarvis;
import com.openarena.openarenaportalapi.dto.JarvisRegistrationRequest;
import com.openarena.openarenaportalapi.service.JarvisService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/jarvis")
public class JarvisController {

    private final JarvisService jarvisService;

    public JarvisController(JarvisService jarvisService) {
        this.jarvisService = jarvisService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerJarvis(@Valid @RequestBody JarvisRegistrationRequest registrationRequest) {
        try {
            Jarvis registeredJarvis = jarvisService.registerNewJarvisUser(registrationRequest);
            // You might want to return a subset of Jarvis data, not the entire entity
            // (e.g., a DTO with just id, username, email).  Avoid returning the password!
            return new ResponseEntity<>(registeredJarvis.getId(), HttpStatus.CREATED); // Return 201 Created with the ID
        } catch (DuplicateResourceException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage(), e);
        }
        // You might have other exceptions (e.g., if the default role is not found). Handle them appropriately.
    }

    @GetMapping("/jobs/count")
    public ResponseEntity<Long> getJobCount() {
        return ResponseEntity.ok(jarvisService.getJobCount());
    }
    @GetMapping("/employers/count")
    public ResponseEntity<Long> getEmployerCount() {
        return ResponseEntity.ok(jarvisService.getEmployerCount());
    }

    @GetMapping("/recruiters/count")
    public ResponseEntity<Long> getRecruiterCount() {
        return ResponseEntity.ok(jarvisService.getRecruiterCount());
    }

    @GetMapping("/jobseekers/count")
    public ResponseEntity<Long> getJobSeekerCount() {
        return ResponseEntity.ok(jarvisService.getJobSeekerCount());
    }
}