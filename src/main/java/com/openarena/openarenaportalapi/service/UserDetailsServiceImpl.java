package com.openarena.openarenaportalapi.service;


import com.openarena.openarenaportalapi.model.JobSeeker;
import com.openarena.openarenaportalapi.model.Recruiter;
import com.openarena.openarenaportalapi.repository.JobSeekerRepository;
import com.openarena.openarenaportalapi.repository.RecruiterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final RecruiterRepository recruiterRepository;
    private final JobSeekerRepository jobSeekerRepository;

    @Autowired
    public UserDetailsServiceImpl(RecruiterRepository recruiterRepository,JobSeekerRepository jobSeekerRepository) {
        this.recruiterRepository = recruiterRepository;
        this.jobSeekerRepository = jobSeekerRepository;
    }

    @Override
    @Transactional(readOnly = true) // Important for lazy loading of roles
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // Try to find by username first
        JobSeeker jobSeeker = jobSeekerRepository.findByUsername(usernameOrEmail).orElse(null);
        // If not found by username, try by email
        if (jobSeeker == null) {
            jobSeeker = jobSeekerRepository.findByEmail(usernameOrEmail)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email : " + usernameOrEmail));
        }

        // Convert our JobSeeker to Spring Security's UserDetails
        return new org.springframework.security.core.userdetails.User(
                jobSeeker.getUsername(), jobSeeker.getPassword(), getAuthorities(jobSeeker));
    }

    private List<GrantedAuthority> getAuthorities(JobSeeker jobSeeker) {
        // Get the roles from the JobSeeker entity. This uses the roles relationship.
        return jobSeeker.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }
}