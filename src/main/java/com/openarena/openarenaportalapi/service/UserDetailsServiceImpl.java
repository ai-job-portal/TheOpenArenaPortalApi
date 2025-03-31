// src/main/java/com/openarena/openarenaportalapi/service/UserDetailsServiceImpl.java
package com.openarena.openarenaportalapi.service;

import com.openarena.openarenaportalapi.model.*;
import com.openarena.openarenaportalapi.repository.JarvisRepository;
import com.openarena.openarenaportalapi.repository.JobSeekerRepository;
import com.openarena.openarenaportalapi.repository.RecruiterRepository;
import com.openarena.openarenaportalapi.util.RequestContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public UserDetailsServiceImpl(RecruiterRepository recruiterRepository, JobSeekerRepository jobSeekerRepository, JarvisRepository jarvisRepository) {
        this.recruiterRepository = recruiterRepository;
        this.jobSeekerRepository = jobSeekerRepository;
        this.jarvisRepository = jarvisRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {

        HttpServletRequest request = RequestContextHolder.getRequest();
        String userType = request != null ? request.getHeader("User-Type") : "none"; // Example: Get from request param

                switch (userType.toLowerCase()) {
                    case "employer":
                        return recruiterRepository.findByUsername(usernameOrEmail)
                                .or(() -> recruiterRepository.findByEmail(usernameOrEmail))
                                .orElseThrow(() -> new UsernameNotFoundException("Recruiter not found: " + usernameOrEmail));
                    case "jobseeker":
                        return jobSeekerRepository.findByUsername(usernameOrEmail)
                                .or(() -> jobSeekerRepository.findByEmail(usernameOrEmail))
                                .orElseThrow(() -> new UsernameNotFoundException("JobSeeker not found: " + usernameOrEmail));
                    case "jarvis":
                        return jarvisRepository.findByUsername(usernameOrEmail)
                                .or(() -> jarvisRepository.findByEmail(usernameOrEmail))
                                .orElseThrow(() -> new UsernameNotFoundException("Jarvis not found: " + usernameOrEmail));
                    default:
                        throw new IllegalArgumentException("Invalid user type: " + userType);
                }
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