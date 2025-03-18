// src/main/java/com/openarena/openarenaportalapi/service/JarvisService.java
package com.openarena.openarenaportalapi.service;

import com.openarena.openarenaportalapi.model.Jarvis;
import com.openarena.openarenaportalapi.dto.JarvisRegistrationRequest;

public interface JarvisService {
    Jarvis registerNewJarvisUser(JarvisRegistrationRequest registrationRequest);
    Long getJobCount();

    Long getEmployerCount();

    Long getRecruiterCount();

    Long getJobSeekerCount();
}