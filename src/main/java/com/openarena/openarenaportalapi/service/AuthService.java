package com.openarena.openarenaportalapi.service;

import com.openarena.openarenaportalapi.dto.*;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    JWTAuthResponse jobSeekerLogin(JobSeekerLoginRequest request);
    JobSeekerRegistrationResponse registerJobSeeker(JobSeekerRegistrationRequest registrationDTO);
    JWTAuthResponse employerLogin(EmployerLoginRequest request);
    EmployerRegistrationResponse registerEmployer(EmployerRegistrationRequestDto request);
    JWTAuthResponse jarvisLogin(JarvisLoginRequest request);
    void logout(HttpServletRequest httpServletRequest);
}