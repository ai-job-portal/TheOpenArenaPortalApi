package com.openarena.openarenaportalapi.service;

import com.openarena.openarenaportalapi.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

public interface AuthService {
    JWTAuthResponse jobSeekerLogin(JobSeekerLoginRequest request);
    JobSeekerRegistrationResponse registerJobSeeker(JobSeekerRegistrationRequest registrationDTO, MultipartFile resumeFile);
    JWTAuthResponse employerLogin(EmployerLoginRequest request);
    EmployerRegistrationResponse registerEmployer(EmployerRegistrationRequestDto request);
    JWTAuthResponse jarvisLogin(JarvisLoginRequest request);
    void logout(HttpServletRequest httpServletRequest);
}