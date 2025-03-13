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

@RestController
@RequestMapping("/jobseekers") // Base path for jobseeker-related operations
@PreAuthorize("hasRole('ROLE_JOBSEEKER')") // Protect all endpoints in this controller
public class JobSeekerController {

    private final JobSeekerRepository jobSeekerRepository;

    @Autowired
    public JobSeekerController(JobSeekerRepository jobSeekerRepository) {
        this.jobSeekerRepository = jobSeekerRepository;
    }

    @GetMapping("/profile")
    public ResponseEntity<JobSeekerResponse> getProfile(Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Should not happen
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        JobSeeker jobSeeker = jobSeekerRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job seeker not found"));

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

        return ResponseEntity.ok(jobSeekerResponse);
    }

}
