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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    @Autowired
    public AuthController(AuthService authService, JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService) {
        this.authService = authService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/jobseeker/login")
    public ResponseEntity<JWTAuthResponse> loginJobSeeker(@RequestBody JobSeekerLoginRequest request) {
        JWTAuthResponse jwtAuthResponse = authService.jobSeekerLogin(request);
        return ResponseEntity.ok(jwtAuthResponse);
    }

    @PostMapping("/employer/login")
    public ResponseEntity<JWTAuthResponse> loginEmployer(@RequestBody EmployerLoginRequest request) {
        JWTAuthResponse jwtAuthResponse = authService.employerLogin(request);
        return ResponseEntity.ok(jwtAuthResponse);
    }

    @PostMapping("/employer/register")
    public ResponseEntity<EmployerRegistrationResponse> registerEmployer(@Valid @RequestBody EmployerRegistrationRequestDto registrationRequest) {
        EmployerRegistrationResponse response = authService.registerEmployer(registrationRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/jarvis/login")
    public ResponseEntity<JWTAuthResponse> loginJarvis(@RequestBody JarvisLoginRequest request) {
        JWTAuthResponse jwtAuthResponse = authService.jarvisLogin(request);
        return ResponseEntity.ok(jwtAuthResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JWTAuthResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        try {
            String refreshToken = refreshTokenRequest.getRefreshToken();
            if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
                String username = jwtTokenProvider.getUsername(refreshToken);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );

                String newAccessToken = jwtTokenProvider.generateToken(newAuthentication);
                String newRefreshToken = jwtTokenProvider.generateRefreshToken(newAuthentication);

                String role = userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(","));
                role = role.substring(role.lastIndexOf("_") + 1).toLowerCase();

                JWTAuthResponse jwtAuthResponse = new JWTAuthResponse();
                jwtAuthResponse.setAccessToken(newAccessToken);
                jwtAuthResponse.setRefreshToken(newRefreshToken);
                jwtAuthResponse.setUsername(userDetails.getUsername());
                jwtAuthResponse.setRole(role);

                return ResponseEntity.ok(jwtAuthResponse);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } catch (ExpiredJwtException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expired", ex);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not refresh token", ex);
        }
    }

    @PostMapping("/jobseeker/register")
    public ResponseEntity<JobSeekerRegistrationResponse> registerJobSeeker(
            @Valid JobSeekerRegistrationRequest registrationDTO, // No @RequestBody, Spring maps form fields
            @RequestParam(value = "resumeFile", required = true) MultipartFile resumeFile) { // "resumeFile" must match FormData key

        JobSeekerRegistrationResponse response = authService.registerJobSeeker(registrationDTO, resumeFile);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // With stateless JWT, logout is client-side (discard token)
        return ResponseEntity.ok("Logged out successfully");
    }
}