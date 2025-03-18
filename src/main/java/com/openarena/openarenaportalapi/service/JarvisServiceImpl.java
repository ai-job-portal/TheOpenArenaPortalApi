// src/main/java/com/openarena/openarenaportalapi/service/JarvisServiceImpl.java
package com.openarena.openarenaportalapi.service;

import com.openarena.openarenaportalapi.exceptions.DuplicateResourceException;
import com.openarena.openarenaportalapi.model.Jarvis;
import com.openarena.openarenaportalapi.model.Role;
import com.openarena.openarenaportalapi.dto.JarvisRegistrationRequest;
import com.openarena.openarenaportalapi.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class JarvisServiceImpl implements JarvisService {

    private final JarvisRepository jarvisRepository;
    private final RoleRepository roleRepository;
    private final RecruiterRepository recruiterRepository;
    private final JobRepository jobRepository;
    private final EmployerRepository employerRepository;
    private final JobSeekerRepository jobSeekerRepository;


    public JarvisServiceImpl(JarvisRepository jarvisRepository, RoleRepository roleRepository,RecruiterRepository recruiterRepository,JobRepository jobRepository,EmployerRepository employerRepository,JobSeekerRepository jobSeekerRepository) {
        this.jarvisRepository = jarvisRepository;
        this.roleRepository = roleRepository;
        this.recruiterRepository=recruiterRepository;
        this.jobRepository=jobRepository;
        this.employerRepository = employerRepository;
        this.jobSeekerRepository=jobSeekerRepository;
    }

    @Override
    @Transactional // Important for data consistency
    public Jarvis registerNewJarvisUser(JarvisRegistrationRequest request) {
        // 1. Check if username or email already exists
        if (jarvisRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already taken");
        }
        if (jarvisRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already taken");
        }

        // 2. Create the Jarvis user
        Jarvis jarvis = new Jarvis();
        jarvis.setUsername(request.getUsername());
        jarvis.setEmail(request.getEmail());
        jarvis.setPassword(request.getPassword()); // Hashing happens in the entity setter

        // 3. Assign default role (e.g., "ROLE_JARVIS")
        Optional<Role> roleOptional = roleRepository.findByName("ROLE_JARVIS"); // Or a different default role
        Role defaultRole = roleOptional.orElseThrow(() -> new IllegalStateException("Default role not found"));
        jarvis.addRole(defaultRole);

        // 4. Save the user
        return jarvisRepository.save(jarvis);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getJobCount() {
        return jobRepository.count(); // Use the count() method from JpaRepository
    }

    @Override
    @Transactional(readOnly = true)
    public Long getEmployerCount() {
        return employerRepository.count(); // Use the count() method from JpaRepository
    }

    @Override
    @Transactional(readOnly = true)
    public Long getRecruiterCount() {
        return recruiterRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getJobSeekerCount() {
        return jobSeekerRepository.count();
    }
}