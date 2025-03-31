package com.openarena.openarenaportalapi.service;

import com.openarena.openarenaportalapi.dto.*;
import com.openarena.openarenaportalapi.exceptions.AlreadyExistException;
import com.openarena.openarenaportalapi.model.*;
import com.openarena.openarenaportalapi.repository.*;
import com.openarena.openarenaportalapi.util.ApiConstants;
import com.openarena.openarenaportalapi.util.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final JobSeekerRepository jobSeekerRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final RecruiterRepository recruiterRepository;
    private final EmployerRepository employerRepository;
    private final RecruiterEmployerRepository recruiterEmployerRepository;
    private final JarvisRepository jarvisRepository;

    @Autowired
    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           JwtTokenProvider jwtTokenProvider,
                           JobSeekerRepository jobSeekerRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           RecruiterRepository recruiterRepository,
                           EmployerRepository employerRepository,
                           RecruiterEmployerRepository recruiterEmployerRepository,
                           JarvisRepository jarvisRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jobSeekerRepository = jobSeekerRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.recruiterRepository = recruiterRepository;
        this.employerRepository = employerRepository;
        this.recruiterEmployerRepository = recruiterEmployerRepository;
        this.jarvisRepository = jarvisRepository;
    }

    @Override
    public JWTAuthResponse jobSeekerLogin(JobSeekerLoginRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                request.getUsernameOrEmail(), request.getPassword()
        );
        authenticationToken.setDetails(request.getUserType());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtTokenProvider.generateToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        Object principal = authentication.getPrincipal();
        JobSeeker jobSeeker = (JobSeeker) principal;

        String role = jobSeeker.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        role = role.substring(role.lastIndexOf("_") + 1).toLowerCase();

        JWTAuthResponse jwtAuthResponse = new JWTAuthResponse();
        jwtAuthResponse.setAccessToken(accessToken);
        jwtAuthResponse.setRefreshToken(refreshToken);
        jwtAuthResponse.setUserId(jobSeeker.getId());
        jwtAuthResponse.setUsername(jobSeeker.getUsername());
        jwtAuthResponse.setName(jobSeeker.getName());
        jwtAuthResponse.setRole(role);

        return jwtAuthResponse;
    }

    @Override
    @Transactional
    public JobSeekerRegistrationResponse registerJobSeeker(JobSeekerRegistrationRequest request) {
        if (jobSeekerRepository.existsByUsername(request.getUsername())) {
            throw new AlreadyExistException(ApiConstants.USERNAME_ALREADY_EXISTS);
        }
        if (jobSeekerRepository.existsByEmail(request.getEmail())) {
            throw new AlreadyExistException(ApiConstants.EMAIL_ALREADY_EXISTS);
        }

        JobSeeker jobSeeker = new JobSeeker();
        jobSeeker.setUsername(request.getUsername());
        jobSeeker.setEmail(request.getEmail());
        jobSeeker.setPassword(passwordEncoder.encode(request.getPassword()));
        jobSeeker.setName(request.getName());
        jobSeeker.setMobile(request.getMobile());
        jobSeeker.setSkills(request.getSkills());
        jobSeeker.setResumeUrl(request.getResumeUrl());

        Role role = roleRepository.findByName("ROLE_JOBSEEKER")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ROLE_JOBSEEKER not found"));
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        jobSeeker.setRoles(roles);

        jobSeeker = jobSeekerRepository.save(jobSeeker);

        return new JobSeekerRegistrationResponse(jobSeeker.getId(), jobSeeker.getUsername(), jobSeeker.getEmail(), "Job seeker registered successfully!");
    }

    @Override
    @Transactional
    public JWTAuthResponse jarvisLogin(JarvisLoginRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                request.getUsernameOrEmail(), request.getPassword()
        );
        authenticationToken.setDetails(request.getUserType());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtTokenProvider.generateToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        Object principal = authentication.getPrincipal();
        Jarvis jarvis = (Jarvis) principal;

        String role = jarvis.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        role = role.substring(role.lastIndexOf("_") + 1).toLowerCase();

        JWTAuthResponse jwtAuthResponse = new JWTAuthResponse();
        jwtAuthResponse.setAccessToken(accessToken);
        jwtAuthResponse.setRefreshToken(refreshToken);
        jwtAuthResponse.setUserId(jarvis.getId());
        jwtAuthResponse.setUsername(jarvis.getUsername());
        jwtAuthResponse.setName(jarvis.getUsername());
        jwtAuthResponse.setRole(role);

        return jwtAuthResponse;
    }

    @Override
    @Transactional
    public JWTAuthResponse employerLogin(EmployerLoginRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                request.getUsernameOrEmail(), request.getPassword()
        );
        authenticationToken.setDetails(request.getUserType());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtTokenProvider.generateToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        Object principal = authentication.getPrincipal();
        Recruiter recruiter = (Recruiter) principal;

        String role = recruiter.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        role = role.substring(role.lastIndexOf("_") + 1).toLowerCase();

        JWTAuthResponse jwtAuthResponse = new JWTAuthResponse();
        jwtAuthResponse.setAccessToken(accessToken);
        jwtAuthResponse.setRefreshToken(refreshToken);
        jwtAuthResponse.setUserId(recruiter.getId());
        jwtAuthResponse.setUsername(recruiter.getUsername());
        jwtAuthResponse.setName(recruiter.getName());
        jwtAuthResponse.setRole(role);

        return jwtAuthResponse;
    }

    @Override
    @Transactional
    public EmployerRegistrationResponse registerEmployer(EmployerRegistrationRequestDto request) {
        if (employerRepository.existsByEmail(request.getEmail())) {
            throw new AlreadyExistException("Company email already registered");
        }
        if (recruiterRepository.existsByUsername(request.getAdminUsername())) {
            throw new AlreadyExistException("Admin username already exists");
        }
        if (recruiterRepository.existsByEmail(request.getAdminEmail())) {
            throw new AlreadyExistException("Admin email already exists");
        }

        Employer newEmployer = new Employer();
        newEmployer.setCompanyName(request.getCompanyName());
        newEmployer.setEmail(request.getEmail());
        newEmployer.setWebsite(request.getWebsite());
        newEmployer.setDescription(request.getDescription());
        newEmployer.setIndustry(request.getIndustry());
        newEmployer.setCompanySize(request.getCompanySize());
        newEmployer.setLocation(request.getLocation());
        newEmployer = employerRepository.save(newEmployer);

        Recruiter newRecruiter = new Recruiter();
        newRecruiter.setUsername(request.getAdminUsername());
        newRecruiter.setEmail(request.getAdminEmail());
        newRecruiter.setPassword(passwordEncoder.encode(request.getAdminPassword()));
        newRecruiter.setName(request.getAdminName());
        newRecruiter.setMobile(request.getAdminMobile());

        Role employerRole = roleRepository.findByName("ROLE_EMPLOYER")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ROLE_EMPLOYER not found"));
        Set<Role> roles = new HashSet<>();
        roles.add(employerRole);
        newRecruiter.setRoles(roles);

        RecruiterEmployer recruiterEmployer = new RecruiterEmployer();
        recruiterEmployer.setEmployer(newEmployer);
        recruiterEmployer.setRecruiter(newRecruiter);
        recruiterEmployer.setRole("admin");

        newRecruiter.getRecruiterEmployers().add(recruiterEmployer);
        newRecruiter.setEmployer(newEmployer);

        newRecruiter = recruiterRepository.save(newRecruiter);
        recruiterEmployerRepository.save(recruiterEmployer);

        return new EmployerRegistrationResponse(
                newEmployer.getId(),
                newEmployer.getCompanyName(),
                newRecruiter.getId(),
                newRecruiter.getUsername(),
                "Employer and admin user registered successfully!"
        );
    }

    @Override
    public void logout(HttpServletRequest request) {
        // No server-side logout needed with stateless JWT
    }
}