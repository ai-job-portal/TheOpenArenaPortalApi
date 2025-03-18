package com.openarena.openarenaportalapi.service;

import com.openarena.openarenaportalapi.dto.*;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    JWTAuthResponse jobSeekerLogin(JobSeekerLoginRequest request, HttpServletRequest httpServletRequest);
    JobSeekerRegistrationResponse registerJobSeeker(JobSeekerRegistrationRequest registrationDTO);
    JWTAuthResponse employerLogin(EmployerLoginRequest request,HttpServletRequest httpServletRequest);
    EmployerRegistrationResponse registerEmployer(EmployerRegistrationRequestDto request);
    JWTAuthResponse jarvisLogin(JarvisLoginRequest request,HttpServletRequest httpServletRequest);
    void logout(HttpServletRequest httpServletRequest);
}