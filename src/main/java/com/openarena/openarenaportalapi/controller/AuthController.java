package com.openarena.openarenaportalapi.controller;


import com.openarena.openarenaportalapi.dto.JobSeekerRegistrationRequest;
import com.openarena.openarenaportalapi.dto.JobSeekerResponse;
import com.openarena.openarenaportalapi.dto.LoginRequestDto;
import com.openarena.openarenaportalapi.dto.LoginResponseDto;
import com.openarena.openarenaportalapi.model.JobSeeker;
import com.openarena.openarenaportalapi.service.JobSeekerService;
import com.openarena.openarenaportalapi.service.UserDetailsServiceImpl;
import com.openarena.openarenaportalapi.util.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.swing.*;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final JobSeekerService jobSeekerService; // Inject JobSeekerService

    @Value("${jwt.expirationMs}")
    private long jwtExpirationMs;

    @Value("${jwt.refreshExpirationMs}")
    private long refreshExpirationMs;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserDetailsServiceImpl userDetailsService,
                          JwtTokenProvider jwtTokenProvider, JobSeekerService jobSeekerService) { // Add to constructor
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jobSeekerService = jobSeekerService; // Initialize
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDto loginRequest,
                                              HttpServletResponse response) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate both access and refresh tokens
            String accessToken = jwtTokenProvider.generateToken(authentication);
            String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

            // Create cookies (HTTP-only and secure)
            Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
            accessTokenCookie.setHttpOnly(true);
            accessTokenCookie.setSecure(false); // Use only with HTTPS
            accessTokenCookie.setPath("/"); // Available to the entire application
            accessTokenCookie.setMaxAge((int) (jwtExpirationMs / 1000)); // Set cookie expiry (in seconds)


            Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(false); // Use only with HTTPS
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge((int) (refreshExpirationMs / 1000)); // Set cookie expiry


            // Add cookies to the response
            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);

            // Return a success response (you could include user details here if needed)
            return ResponseEntity.ok(new LoginResponseDto(accessToken));

        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(Map.of("message", "Incorrect username or password"),
                    HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            // Log the exception
            return new ResponseEntity<>(Map.of("message", "An unexpected error occurred"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            String refreshToken = extractRefreshTokenFromCookie(request);
            if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
                String username = jwtTokenProvider.getUsername(refreshToken);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                // Generate new access token
                String newAccessToken = jwtTokenProvider.generateToken(
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
                        // Create Authentication object from UserDetails
                );

                // Update the access token cookie
                Cookie accessTokenCookie = new Cookie("accessToken", newAccessToken);
                accessTokenCookie.setHttpOnly(true);
                accessTokenCookie.setSecure(false);
                accessTokenCookie.setPath("/");
                accessTokenCookie.setMaxAge((int) (jwtExpirationMs / 1000));
                response.addCookie(accessTokenCookie);

                return ResponseEntity.ok(new LoginResponseDto(newAccessToken));
            } else {
                return new ResponseEntity<>(Map.of("message", "Invalid refresh token"), HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            // Log exception
            return new ResponseEntity<>(Map.of("message", "Could not refresh token"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Helper method to extract refresh token from cookies
    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // Invalidate access token cookie
        Cookie accessTokenCookie = new Cookie("accessToken", null); // Set to null to remove
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(false);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(0); // Set max age to 0 to delete it immediately
        response.addCookie(accessTokenCookie);

        // Invalidate refresh token cookie
        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0); // Set max age to 0 to delete immediately
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    // --- Jobseeker Registration ---
    @PostMapping("/register/jobseeker")
    public ResponseEntity<?> registerJobSeeker(
            @Valid @RequestBody JobSeekerRegistrationRequest registrationRequest) {
        JobSeekerResponse response = jobSeekerService.registerJobSeeker(registrationRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}