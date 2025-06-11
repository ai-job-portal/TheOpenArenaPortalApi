package com.openarena.openarenaportalapi.controller;


import com.openarena.openarenaportalapi.dto.CreateJobDto;
import com.openarena.openarenaportalapi.dto.JobDto;
import com.openarena.openarenaportalapi.dto.UpdateJobDto;
import com.openarena.openarenaportalapi.model.Employer;
import com.openarena.openarenaportalapi.model.Recruiter;
import com.openarena.openarenaportalapi.repository.RecruiterRepository;
import com.openarena.openarenaportalapi.service.JobService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/jobs") // Add a base path for all job-related endpoints
public class JobController {

    private final JobService jobService;
    private final RecruiterRepository recruiterRepository;

    @Autowired
    public JobController(JobService jobService, RecruiterRepository recruiterRepository) {
        this.jobService = jobService;
        this.recruiterRepository = recruiterRepository;
    }

    @GetMapping
    public List<JobDto> getAllJobs() {
        return jobService.getAllJobs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobDto> getJobById(@PathVariable Integer id) {
        Optional<JobDto> jobDto = jobService.getJobById(id);
        return jobDto.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_RECRUITER', 'ROLE_EMPLOYER')")
    public ResponseEntity<JobDto> createJob(@Valid @RequestBody CreateJobDto createJobDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer recruiterId = null;

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            // Check if the principal is your Recruiter entity
            // Adjust the class 'Recruiter.class' and 'getId()' to match your actual entity
            if (principal instanceof Recruiter) {
                recruiterId = ((Recruiter) principal).getId(); // Assuming getId() returns Integer
            }
            else if (principal instanceof Employer) {
              recruiterId = ((Employer) principal).getId();
            }
            // Add more 'else if' blocks if other principal types can represent a recruiter
        }

        if (recruiterId == null) {
            // This means after checking all conditions, we couldn't get a recruiterId.
            // This should ideally not happen if @PreAuthorize works and your principal is set correctly.
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Could not determine Recruiter ID for job posting. User may not be a valid Recruiter or Employer type for this action.");
        }

        JobDto createdJob = jobService.createJob(createJobDto, recruiterId);
        return new ResponseEntity<>(createdJob, HttpStatus.CREATED); // Return 201 Created
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_RECRUITER', 'ROLE_EMPLOYER')")
    public ResponseEntity<JobDto> updateJob(@PathVariable Integer id, @Valid @RequestBody UpdateJobDto updateJobDto) {
        JobDto updatedJob = jobService.updateJob(id, updateJobDto);
        return ResponseEntity.ok(updatedJob); // Return 200 OK with updated job
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Integer id) {
        jobService.deleteJob(id);
        return ResponseEntity.noContent().build(); // Return 204 No Content
    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<JobDto> restoreJob(@PathVariable Integer id) {
        JobDto updatedJob = jobService.restoreJob(id);
        return ResponseEntity.ok(updatedJob); // Return 200 JobDto
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasAnyRole('ROLE_RECRUITER', 'ROLE_EMPLOYER')")
    public ResponseEntity<List<JobDto>> createJobs(@Valid @RequestBody List<CreateJobDto> createJobDtos) {
        // TODO: get recruiter id from Authentication token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Recruiter recruiter = recruiterRepository.findByUsername(username).get();
        Integer recruiterId = recruiter.getId();
        List<JobDto> createdJobs = new ArrayList<>();
        for (CreateJobDto dto : createJobDtos) {
            createdJobs.add(jobService.createJob(dto, recruiterId));
        }
        return new ResponseEntity<>(createdJobs, HttpStatus.CREATED);
    }
}
