package com.openarena.openarenaportalapi.controller;

import com.openarena.openarenaportalapi.dto.JobSeekerResponse;
import com.openarena.openarenaportalapi.model.JobSeeker;
import com.openarena.openarenaportalapi.repository.JobSeekerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/users") // Good base path for user-related operations
public class UserController {

    private final JobSeekerRepository jobSeekerRepository;

    @Autowired
    public UserController(JobSeekerRepository jobSeekerRepository) {
        this.jobSeekerRepository = jobSeekerRepository;
    }

    @GetMapping("/me") // Good, descriptive endpoint name
    @PreAuthorize("hasAnyRole('ROLE_JOBSEEKER', 'ROLE_RECRUITER', 'ROLE_EMPLOYER', 'ROLE_JARVIS')") // Protect!
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Should not happen, but good to check
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        // Fetch User/Recruiter/JobSeeker details from DB based on the principal
        // For JobSeeker
        Optional<JobSeeker> optionalJobSeeker = jobSeekerRepository.findByUsername(username);
        if(optionalJobSeeker.isPresent()) {
            JobSeeker jobSeeker = optionalJobSeeker.get();
            JobSeekerResponse jobSeekerResponse = new JobSeekerResponse();
            jobSeekerResponse.setId(jobSeeker.getId());
            jobSeekerResponse.setUsername(jobSeeker.getUsername());
            jobSeekerResponse.setEmail(jobSeeker.getEmail());
            jobSeekerResponse.setName(jobSeeker.getName());
            jobSeekerResponse.setMobile(jobSeeker.getMobile());
            if(jobSeeker.getSkills() != null) {
                jobSeekerResponse.setSkills(jobSeeker.getSkills());
            }
            if(jobSeeker.getResumeUrl() != null){
                jobSeekerResponse.setResumeUrl(jobSeeker.getResumeUrl());
            }

            return ResponseEntity.ok(jobSeekerResponse); // Return 200 OK with details
        }

        //For other roles create like this.
        // Check if the user is a Recruiter
        //  Optional<Recruiter> optionalRecruiter = recruiterRepository.findByUsername(username);
        //   if (optionalRecruiter.isPresent()) {
        //          //return details
        //    }
        //similarly for employer and Jarvis.

        // If we don't find them, we throw error, or we can return 404.
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

    }
}