package com.openarena.openarenaportalapi.service;

import com.openarena.openarenaportalapi.model.*;
import com.openarena.openarenaportalapi.repository.JarvisRepository;
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

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final RecruiterRepository recruiterRepository;
    private final JobSeekerRepository jobSeekerRepository;
    private final JarvisRepository jarvisRepository;

    @Autowired
    public UserDetailsServiceImpl(RecruiterRepository recruiterRepository, JobSeekerRepository jobSeekerRepository,JarvisRepository jarvisRepository) {
        this.recruiterRepository = recruiterRepository;
        this.jobSeekerRepository = jobSeekerRepository;
        this.jarvisRepository = jarvisRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // Try to find as a Recruiter (Employer Login) first by username
        Recruiter recruiter = recruiterRepository.findByUsername(usernameOrEmail).orElse(null);
        if (recruiter != null) {
            return recruiter; // Return Recruiter directly
        }

        // If not found as a Recruiter, try as a JobSeeker by username
        JobSeeker jobSeeker = jobSeekerRepository.findByUsername(usernameOrEmail).orElse(null);
        if (jobSeeker != null) {
            return jobSeeker; // Return JobSeeker directly
        }

        // Try Jarvis by username
        Jarvis jarvis = jarvisRepository.findByUsername(usernameOrEmail).orElse(null);
        if (jarvis != null) {
            return jarvis; // Return directly, Jarvis implements UserDetails
        }

        recruiter = recruiterRepository.findByEmail(usernameOrEmail).orElse(null);
        if (recruiter != null) {
            return recruiter;
        }

        // Finally try as a JobSeeker by email
        jobSeeker = jobSeekerRepository.findByEmail(usernameOrEmail).orElse(null);
        if (jobSeeker != null) {
            return jobSeeker;
        }
        //Finally try to find jarvis by email
        jarvis = jarvisRepository.findByEmail(usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail));
        return jarvis; // Return directly, Jarvis implements UserDetails
    }

    // Helper method to create UserDetails from *any* User entity
    private UserDetails buildUserDetails(User user) {
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), getAuthorities(user));
    }

    // Helper method to get authorities (roles) from *any* User entity
    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().toUpperCase())) //Consistent role names
                .collect(Collectors.toList());
    }
}