package com.openarena.openarenaportalapi.controller;

import com.openarena.openarenaportalapi.dto.*;
import com.openarena.openarenaportalapi.service.AuthService;
import com.openarena.openarenaportalapi.util.JwtTokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest; // Import HttpServletRequest
import jakarta.servlet.http.HttpServletResponse; // Import HttpServletResponse
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private static final String ACCESS_TOKEN_KEY = "accessToken";
    private static final String REFRESH_TOKEN_KEY = "refreshToken";

    @Autowired
    public AuthController(AuthService authService, JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService) {
        this.authService = authService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    // Build Login REST API
    @PostMapping("/jobseeker/login")
    public ResponseEntity<JWTAuthResponse> loginJobSeeker(@RequestBody JobSeekerLoginRequest request, HttpServletRequest httpServletRequest, HttpServletResponse response) {
        JWTAuthResponse jwtAuthResponse = authService.jobSeekerLogin(request, httpServletRequest); // Pass HttpServletRequest
        return ResponseEntity.ok(jwtAuthResponse); // Return token in body
    }

    @PostMapping("/employer/login")
    public ResponseEntity<JWTAuthResponse> loginEmployer(@RequestBody EmployerLoginRequest request, HttpServletRequest httpServletRequest, HttpServletResponse response) {
        JWTAuthResponse jwtAuthResponse = authService.employerLogin(request, httpServletRequest); // Pass HttpServletRequest
        return ResponseEntity.ok(jwtAuthResponse);  // Return token in body
    }

    // Build Register REST API for Employer
    @PostMapping("/employer/register")
    public ResponseEntity<EmployerRegistrationResponse> registerEmployer(@Valid @RequestBody EmployerRegistrationRequestDto registrationRequest) {
        EmployerRegistrationResponse response = authService.registerEmployer(registrationRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Build Login REST API for Jarvis
    @PostMapping("/jarvis/login")
    public ResponseEntity<JWTAuthResponse> loginJarvis(@RequestBody JarvisLoginRequest request, HttpServletRequest httpServletRequest, HttpServletResponse response) {
        JWTAuthResponse jwtAuthResponse = authService.jarvisLogin(request, httpServletRequest); // Pass HttpServletRequest
        return ResponseEntity.ok(jwtAuthResponse); // Return token in body
    }


    @PostMapping("/refresh")
    public ResponseEntity<JWTAuthResponse> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = request.getSession(false);  // Do NOT create a new session
            if(session == null){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            String refreshToken = (String) session.getAttribute(REFRESH_TOKEN_KEY);

            if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
                String username = jwtTokenProvider.getUsername(refreshToken);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );

                String newAccessToken = jwtTokenProvider.generateToken(newAuthentication);
                String newRefreshToken = jwtTokenProvider.generateRefreshToken(newAuthentication); // Generate new refresh token

                String role = userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(","));
                role = role.substring(role.lastIndexOf("_") + 1).toLowerCase();

                JWTAuthResponse jwtAuthResponse = new JWTAuthResponse(); // Use the DTO
                jwtAuthResponse.setAccessToken(newAccessToken);
                jwtAuthResponse.setRefreshToken(newRefreshToken);
                jwtAuthResponse.setUsername(userDetails.getUsername());
                jwtAuthResponse.setRole(role);
                // Do NOT set userId here. We'll get it from /users/me

                // Store *new* tokens in session
                session.setAttribute(ACCESS_TOKEN_KEY, newAccessToken);
                session.setAttribute(REFRESH_TOKEN_KEY, newRefreshToken);

                return ResponseEntity.ok(jwtAuthResponse);


            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // Or throw exception
            }
        } catch (ExpiredJwtException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expired", ex);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not refresh token", ex);
        }
    }


    @PostMapping("/jobseeker/register")
    public ResponseEntity<JobSeekerRegistrationResponse> registerJobSeeker(@Valid @RequestBody JobSeekerRegistrationRequest registrationDTO) {
        JobSeekerRegistrationResponse response = authService.registerJobSeeker(registrationDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) { // Add HttpServletRequest
        authService.logout(request); // Call logout method
        return ResponseEntity.ok("Logged out successfully");
    }
}