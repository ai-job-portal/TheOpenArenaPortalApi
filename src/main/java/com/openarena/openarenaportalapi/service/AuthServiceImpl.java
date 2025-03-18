// src/main/java/com/openarena/openarenaportalapi/service/AuthServiceImpl.java
package com.openarena.openarenaportalapi.service;

import com.openarena.openarenaportalapi.dto.*;
import com.openarena.openarenaportalapi.exceptions.AlreadyExistException;
import com.openarena.openarenaportalapi.exceptions.NotFoundException;
import com.openarena.openarenaportalapi.model.*;
import com.openarena.openarenaportalapi.repository.*;
import com.openarena.openarenaportalapi.util.ApiConstants;
import com.openarena.openarenaportalapi.util.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest; // Import HttpServletRequest
import jakarta.servlet.http.HttpSession; // Import HttpSession
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService{

    private AuthenticationManager authenticationManager;
    private  JwtTokenProvider jwtTokenProvider;

    private final JobSeekerRepository jobSeekerRepository;
    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;
    private final RecruiterRepository recruiterRepository;
    private final EmployerRepository employerRepository; // Inject
    private final RecruiterEmployerRepository recruiterEmployerRepository; // Inject
    private final JarvisRepository jarvisRepository;
    private static final String ACCESS_TOKEN_KEY = "accessToken"; // Key for session storage
    private static final String REFRESH_TOKEN_KEY = "refreshToken";


    @Autowired
    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           JwtTokenProvider jwtTokenProvider,
                           JobSeekerRepository jobSeekerRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           RecruiterRepository recruiterRepository,
                           EmployerRepository employerRepository,
                           RecruiterEmployerRepository recruiterEmployerRepository,
                           JarvisRepository jarvisRepository) { // Add to constructor
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jobSeekerRepository = jobSeekerRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.recruiterRepository = recruiterRepository;
        this.employerRepository = employerRepository;
        this.recruiterEmployerRepository = recruiterEmployerRepository;
        this.jarvisRepository = jarvisRepository; // Initialize
    }

    @Override
    public JWTAuthResponse jobSeekerLogin(JobSeekerLoginRequest request, HttpServletRequest httpServletRequest) { // Add HttpServletRequest

        HttpSession existingSession = httpServletRequest.getSession(false);
        if (existingSession != null) {
            existingSession.invalidate();
        }

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsernameOrEmail(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        //Create new Session
        HttpSession newSession = httpServletRequest.getSession(true);
        newSession.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());


        String accessToken = jwtTokenProvider.generateToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        // *** Store tokens in session ***
        newSession.setAttribute(ACCESS_TOKEN_KEY, accessToken);
        newSession.setAttribute(REFRESH_TOKEN_KEY, refreshToken);


        // Retrieve user details from the principal *correctly*
        Object principal = authentication.getPrincipal();
        JobSeeker jobSeeker;

        if (principal instanceof JobSeeker) {
            jobSeeker = (JobSeeker) principal;
        } else {
            // This should *never* happen if UserDetailsServiceImpl is correct.
            throw new IllegalStateException("Unexpected principal type: " + principal.getClass().getName());
        }

        // Get the role from the JobSeeker object.
        String role = jobSeeker.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        // Extract actual role by removing "ROLE_"
        role = role.substring(role.lastIndexOf("_") + 1).toLowerCase();

        // Create the response DTO
        JWTAuthResponse jwtAuthResponse = new JWTAuthResponse();
        jwtAuthResponse.setAccessToken(accessToken);  //Still return in response for first login.
        jwtAuthResponse.setRefreshToken(refreshToken);
        jwtAuthResponse.setUserId(jobSeeker.getId());
        jwtAuthResponse.setUsername(jobSeeker.getUsername());
        jwtAuthResponse.setName(jobSeeker.getName());
        jwtAuthResponse.setRole(role); // *** SET THE ROLE ***

        return jwtAuthResponse;
    }

    @Override
    @Transactional // Important for data consistency
    public JobSeekerRegistrationResponse registerJobSeeker(JobSeekerRegistrationRequest request) {

        // Check if username already exists
        if (jobSeekerRepository.existsByUsername(request.getUsername())) {
            throw new AlreadyExistException(ApiConstants.USERNAME_ALREADY_EXISTS);
        }

        // Check if email already exists
        if (jobSeekerRepository.existsByEmail(request.getEmail())) {
            throw new AlreadyExistException(ApiConstants.EMAIL_ALREADY_EXISTS);
        }

        // Create new JobSeeker entity
        JobSeeker jobSeeker = new JobSeeker();
        jobSeeker.setUsername(request.getUsername());
        jobSeeker.setEmail(request.getEmail());
        jobSeeker.setPassword(request.getPassword()); // Set the *plain text* password
        jobSeeker.setName(request.getName());
        jobSeeker.setMobile(request.getMobile());
        jobSeeker.setSkills(request.getSkills());
        jobSeeker.setResumeUrl(request.getResumeUrl());

        // Fetch the ROLE_JOBSEEKER role
        Role role = roleRepository.findByName("ROLE_JOBSEEKER")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ROLE_JOBSEEKER not found"));

        // Set the role to the jobseeker
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        jobSeeker.setRoles(roles);

        // Save the new jobseeker to the database
        jobSeeker = jobSeekerRepository.save(jobSeeker); // Get the saved entity (with ID)

        // Create the response DTO
        return new JobSeekerRegistrationResponse(jobSeeker.getId(), jobSeeker.getUsername(), jobSeeker.getEmail(), "Job seeker registered successfully!");
    }

    @Override
    @Transactional
    public JWTAuthResponse jarvisLogin(JarvisLoginRequest request, HttpServletRequest httpServletRequest) { // Add HttpServletRequest

        HttpSession existingSession = httpServletRequest.getSession(false);
        if (existingSession != null) {
            existingSession.invalidate();
        }

        // 1. Authenticate using AuthenticationManager
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsernameOrEmail(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        HttpSession newSession = httpServletRequest.getSession(true);
        newSession.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());


        String accessToken = jwtTokenProvider.generateToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        // *** Store tokens in session ***
        newSession.setAttribute(ACCESS_TOKEN_KEY, accessToken);
        newSession.setAttribute(REFRESH_TOKEN_KEY, refreshToken);



        // 3.  Retrieve *Jarvis* details. Use getPrincipal()!
        Object principal = authentication.getPrincipal();
        Jarvis jarvis;

        if (principal instanceof Jarvis) {
            jarvis = (Jarvis) principal;
        } else {
            // Handle this unexpected case. This should NEVER happen if UserDetailsServiceImpl is correct.
            throw new IllegalStateException("Unexpected principal type: " + principal.getClass().getName());
        }
        //add role if no role is present for the user.  //REMOVED THIS
        //if(jarvis.getAuthorities().isEmpty()){
        //    Role roles = roleRepository.findByName("ROLE_JARVIS").get();
        //    jarvis.addRole(roles);
        //}

        // Get the role from the Jarvis object.  Crucially, use getAuthorities().
        String role = jarvis.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        //remove "ROLE_"
        role = role.substring(role.lastIndexOf("_") + 1).toLowerCase();


        // 4. Create the response DTO
        JWTAuthResponse jwtAuthResponse = new JWTAuthResponse();
        jwtAuthResponse.setAccessToken(accessToken); //Still return in response for first login.
        jwtAuthResponse.setRefreshToken(refreshToken);
        jwtAuthResponse.setUserId(jarvis.getId());  // Set jarvis ID
        jwtAuthResponse.setUsername(jarvis.getUsername()); // Set username
        jwtAuthResponse.setName(jarvis.getUsername()); // You don't have a name field in Jarvis, using username
        jwtAuthResponse.setRole(role); // *** SET THE ROLE ***

        return jwtAuthResponse;
    }

    @Override
    @Transactional
    public JWTAuthResponse employerLogin(EmployerLoginRequest request, HttpServletRequest httpServletRequest) {// Add HttpServletRequest

        HttpSession existingSession = httpServletRequest.getSession(false);
        if (existingSession != null) {
            existingSession.invalidate();
        }

        // 1. Authenticate using AuthenticationManager
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsernameOrEmail(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        HttpSession newSession = httpServletRequest.getSession(true);
        newSession.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());


        String accessToken = jwtTokenProvider.generateToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        // *** Store tokens in session ***
        newSession.setAttribute(ACCESS_TOKEN_KEY, accessToken);
        newSession.setAttribute(REFRESH_TOKEN_KEY, refreshToken);



        Object principal = authentication.getPrincipal();
        Recruiter recruiter;

        if (principal instanceof Recruiter) {
            recruiter = (Recruiter) principal;
        } else {
            throw new IllegalStateException("Unexpected principal type: " + principal.getClass().getName());
        }

        // Get the role from the Recruiter object.  Crucially, use getAuthorities().
        String role = recruiter.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        // Or, if you *know* it's a single role, and you want to remove the "ROLE_" prefix:
        role = role.substring(role.lastIndexOf("_") + 1).toLowerCase();

        JWTAuthResponse jwtAuthResponse = new JWTAuthResponse();
        jwtAuthResponse.setAccessToken(accessToken); //Still return in response for first login.
        jwtAuthResponse.setRefreshToken(refreshToken);
        jwtAuthResponse.setUserId(recruiter.getId());
        jwtAuthResponse.setUsername(recruiter.getUsername());
        jwtAuthResponse.setName(recruiter.getName());
        jwtAuthResponse.setRole(role); // *** SET THE ROLE ***

        return jwtAuthResponse;
    }

    @Override
    @Transactional
    public EmployerRegistrationResponse registerEmployer(EmployerRegistrationRequestDto request) {
        // Check for duplicate company email
        if (employerRepository.existsByEmail(request.getEmail())) {
            throw new AlreadyExistException("Company email already registered");
        }

        // Check for duplicate admin username and email
        if (recruiterRepository.existsByUsername(request.getAdminUsername())) {
            throw new AlreadyExistException("Admin username already exists");
        }
        if (recruiterRepository.existsByEmail(request.getAdminEmail())) {
            throw new AlreadyExistException("Admin email already exists");
        }

        // 1. Create the Employer entity
        Employer newEmployer = new Employer();
        newEmployer.setCompanyName(request.getCompanyName());
        newEmployer.setEmail(request.getEmail());
        newEmployer.setWebsite(request.getWebsite());
        newEmployer.setDescription(request.getDescription());
        newEmployer.setIndustry(request.getIndustry());
        newEmployer.setCompanySize(request.getCompanySize());
        newEmployer.setLocation(request.getLocation());
        newEmployer = employerRepository.save(newEmployer); // Save to get the ID


        // 2. Create the Admin Recruiter entity
        Recruiter newRecruiter = new Recruiter();
        newRecruiter.setUsername(request.getAdminUsername());
        newRecruiter.setEmail(request.getAdminEmail());
        newRecruiter.setPassword(passwordEncoder.encode(request.getAdminPassword())); // Hash the password
        newRecruiter.setName(request.getAdminName());
        newRecruiter.setMobile(request.getAdminMobile());

        // 3. Set up the roles
        Role employerRole = roleRepository.findByName("ROLE_EMPLOYER")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ROLE_EMPLOYER not found"));
        Role recruiterRole = roleRepository.findByName("ROLE_RECRUITER") //Also assign recruiter role
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ROLE_RECRUITER not found"));
        Set<Role> roles = new HashSet<>();
        roles.add(employerRole);
        //roles.add(recruiterRole);
        newRecruiter.setRoles(roles); //Set the roles


        // 4. Create and set RecruiterEmployer *BEFORE* saving Recruiter
        RecruiterEmployer recruiterEmployer = new RecruiterEmployer();
        recruiterEmployer.setEmployer(newEmployer);
        recruiterEmployer.setRecruiter(newRecruiter);
        recruiterEmployer.setRole("admin"); // Set the role to "admin"

        // 5. Set up relationships *BEFORE* saving Recruiter
        newRecruiter.getRecruiterEmployers().add(recruiterEmployer);
        newRecruiter.setEmployer(newEmployer);  // Set the direct @ManyToOne relationship

        // persist associations
        newRecruiter = recruiterRepository.save(newRecruiter); // Save Recruiter *with* the employer association
        recruiterEmployerRepository.save(recruiterEmployer); // Save RecruiterEmployer


        // 6. Generate an invitation code (You can add this logic back if needed)
        String invitationCode = UUID.randomUUID().toString(); // Simple UUID for now


        return new EmployerRegistrationResponse(
                newEmployer.getId(),
                newEmployer.getCompanyName(),
                newRecruiter.getId(),
                newRecruiter.getUsername(),
                "Employer and admin user registered successfully!"
        );
    }

    //Add DTO and create logout endpoint
    @Override
    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // Don't create if it doesn't exist
        if (session != null) {
            session.invalidate(); // Invalidate the session, removing the token
        }
    }
}