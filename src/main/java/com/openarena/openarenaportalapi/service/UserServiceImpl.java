package com.openarena.openarenaportalapi.service;

import com.openarena.openarenaportalapi.model.*;
import com.openarena.openarenaportalapi.dto.UserResponse;
import com.openarena.openarenaportalapi.repository.JobSeekerRepository;
import com.openarena.openarenaportalapi.repository.RecruiterRepository;
import com.openarena.openarenaportalapi.repository.JarvisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final RecruiterRepository recruiterRepository;
    private final JobSeekerRepository jobSeekerRepository;
    private final JarvisRepository jarvisRepository;

    @Autowired
    public UserServiceImpl(RecruiterRepository recruiterRepository,
                           JobSeekerRepository jobSeekerRepository,
                           JarvisRepository jarvisRepository) {
        this.recruiterRepository = recruiterRepository;
        this.jobSeekerRepository = jobSeekerRepository;
        this.jarvisRepository = jarvisRepository;
    }

    @Override
    public UserResponse getMe() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        Object principal = authentication.getPrincipal();
        String role = null;
        if (principal instanceof UserDetails) {
            // Get roles from the principal (UserDetails)
            role = ((UserDetails) principal).getAuthorities().stream()
                    .map(grantedAuthority -> grantedAuthority.getAuthority())
                    .collect(Collectors.joining(",")); // "ROLE_USER,ROLE_ADMIN"
            //Since role will be like "ROLE_JARVIS", so need to extract.
            role = role.substring(role.lastIndexOf("_") + 1).toLowerCase(); // Extract "jarvis", "employer", etc.
        }

        if (principal instanceof Recruiter) {
            Recruiter recruiter = (Recruiter) principal;
            return new UserResponse(recruiter.getId(), recruiter.getUsername(), recruiter.getName(), role); //Pass role
        } else if (principal instanceof JobSeeker) {
            JobSeeker jobSeeker = (JobSeeker) principal;
            return new UserResponse(jobSeeker.getId(), jobSeeker.getUsername(), jobSeeker.getName(), role); //Pass role
        } else if (principal instanceof Jarvis) {
            Jarvis jarvis = (Jarvis) principal;
            return new UserResponse(jarvis.getId(), jarvis.getUsername(), jarvis.getUsername(), role);//Pass role
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
    }
}
