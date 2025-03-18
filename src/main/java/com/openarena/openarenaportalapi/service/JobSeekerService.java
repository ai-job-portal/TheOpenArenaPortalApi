/*
package com.openarena.openarenaportalapi.service;


import com.openarena.openarenaportalapi.dto.JobSeekerRegistrationRequest;
import com.openarena.openarenaportalapi.dto.JobSeekerResponse;
import com.openarena.openarenaportalapi.model.JobSeeker;
import com.openarena.openarenaportalapi.model.Role;
import com.openarena.openarenaportalapi.exceptions.AlreadyExistException;
import com.openarena.openarenaportalapi.exceptions.NotFoundException;
import com.openarena.openarenaportalapi.repository.JobSeekerRepository;
import com.openarena.openarenaportalapi.repository.RoleRepository;
import com.openarena.openarenaportalapi.util.ApiConstants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
public class JobSeekerService {

    private final JobSeekerRepository jobSeekerRepository;
    private final RoleRepository roleRepository;

    public JobSeekerService(JobSeekerRepository jobSeekerRepository, RoleRepository roleRepository) {
        this.jobSeekerRepository = jobSeekerRepository;
        this.roleRepository = roleRepository;
    }
    @Transactional // Important for managing the Hibernate session
    public JobSeekerResponse registerJobSeeker(JobSeekerRegistrationRequest request) {
        if (jobSeekerRepository.existsByUsername(request.getUsername())) {
            throw new AlreadyExistException(ApiConstants.USERNAME_ALREADY_EXISTS);
        }
        if (jobSeekerRepository.existsByEmail(request.getEmail())) {
            throw new AlreadyExistException(ApiConstants.EMAIL_ALREADY_EXISTS);
        }

        JobSeeker jobSeeker = new JobSeeker();
        jobSeeker.setUsername(request.getUsername());
        jobSeeker.setEmail(request.getEmail());
        jobSeeker.setPassword(request.getPassword()); // Hashing handled in JobSeeker entity
        jobSeeker.setName(request.getName());
        jobSeeker.setMobile(request.getMobile());
        if (request.getSkills() != null) {
            jobSeeker.setSkills(request.getSkills());
        }
        if (request.getResumeUrl() != null) {
            jobSeeker.setResumeUrl(request.getResumeUrl());
        }

        // --- FIX: Load the existing Role from the database ---
        Role userRole = roleRepository.findByName("ROLE_JOBSEEKER")
                .orElseThrow(() -> new NotFoundException("Role 'ROLE_JOBSEEKER' not found")); // Handle the case where the role doesn't exist
        jobSeeker.getRoles().add(userRole); // Add the *persistent* role to the JobSeeker.

        // --- Now save the JobSeeker ---
        JobSeeker savedJobSeeker = jobSeekerRepository.save(jobSeeker); // No more detached entity error!

        return new JobSeekerResponse(
                savedJobSeeker.getId(),
                savedJobSeeker.getUsername(),
                savedJobSeeker.getEmail(),
                savedJobSeeker.getName(),
                savedJobSeeker.getSkills(),
                savedJobSeeker.getResumeUrl(),
                savedJobSeeker.getMobile()
        );
    }
}*/
