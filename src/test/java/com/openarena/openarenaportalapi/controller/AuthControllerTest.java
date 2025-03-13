package com.openarena.openarenaportalapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openarena.openarenaportalapi.dto.LoginRequestDto;
import com.openarena.openarenaportalapi.dto.LoginResponseDto;
import com.openarena.openarenaportalapi.model.Employer;
import com.openarena.openarenaportalapi.model.Recruiter;
import com.openarena.openarenaportalapi.model.Role;
import com.openarena.openarenaportalapi.repository.EmployerRepository;
import com.openarena.openarenaportalapi.repository.RecruiterRepository;
import com.openarena.openarenaportalapi.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Important for rolling back database changes
@ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RecruiterRepository recruiterRepository; // Use RecruiterRepository

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EmployerRepository employerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper; // For converting objects to JSON

    private static final String BASE_URL = "/api/auth";

    private String jwtToken; // to store jwt token.

    @BeforeEach
    void setUp() throws Exception { // Add throws Exception
        // Clean up (optional, but good practice)
        recruiterRepository.deleteAll();
        roleRepository.deleteAll();
        employerRepository.deleteAll();

        // Create a Role
        Role recruiterRole = new Role(null,"ROLE_RECRUITER");
        recruiterRole = roleRepository.save(recruiterRole);
        Set<Role> roles = new HashSet<>();
        roles.add(recruiterRole);

        // Create a test Employer
        Employer employer = new Employer();
        employer.setCompanyName("Test Company");
        employer.setEmail("employer@test.com"); // must be unique
        employer.setIndustry("IT");
        employer.setLocation("Test Location");
        employer.setCompanySize("1-10"); // Set other fields
        employer.setWebsite("test.com");
        employer = employerRepository.save(employer); // Save to get the ID


        // Create a test Recruiter
        Recruiter recruiter = new Recruiter();
        recruiter.setUsername("testuser");
        recruiter.setEmail("test@example.com"); // must be unique
        recruiter.setPassword(passwordEncoder.encode("password"));
        recruiter.setName("Test User");
        recruiter.setMobile("1234567890");
        recruiter.setEmployer(employer); // Associate with the employer
        recruiter.setRoles(roles);
        recruiterRepository.save(recruiter);

        // Perform login and get JWT token.
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setUsername("testuser");
        loginRequestDto.setPassword("password");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = result.getResponse().getContentAsString();

        // Extract JWT from the response (This is a bit fragile, a better approach is to use a JSON parsing library)
        jwtToken = objectMapper.readValue(contentAsString, LoginResponseDto.class).getJwt();

    }


    @Test
    void testLogin_Success() throws Exception {
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").exists()); // Use "jwt" as the field name
    }
    @Test
    void testLogin_Failure_BadCredentials() throws Exception {
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("wrongpassword"); // Incorrect password

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
    @Test
    void testLogin_Failure_UserNotFound() throws Exception {
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setUsername("nonexistentuser"); // User doesn't exist
        loginRequest.setPassword("password");

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testProtectedEndpoint_WithValidToken() throws Exception {
        // Make a request to a protected endpoint, including the JWT in the Authorization header
        mockMvc.perform(MockMvcRequestBuilders.get("/api/jobs") // Replace with your protected endpoint
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk()); // Or whatever status you expect for a successful request
    }

    @Test
    void testProtectedEndpoint_WithoutToken() throws Exception {
        // Make a request to the *same* protected endpoint, but *without* the Authorization header
        mockMvc.perform(MockMvcRequestBuilders.get("/api/jobs")) // Replace with your protected endpoint
                .andExpect(status().isUnauthorized()); // Expect a 401 Unauthorized error
    }
}
