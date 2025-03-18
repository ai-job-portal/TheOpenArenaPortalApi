// src/main/java/com/openarena/openarenaportalapi/config/AuditorAwareImpl.java
package com.openarena.openarenaportalapi.config;

import com.openarena.openarenaportalapi.model.User;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
public class AuditorAwareImpl implements AuditorAware<Integer> {

    @Override
    public Optional<Integer> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty(); // Or Optional.of(someDefaultUserId)
        }
        // Get the UserDetails (which is your User entity)
        Object principal = authentication.getPrincipal();

        if (principal instanceof User) {
            return Optional.of(((User) principal).getId());
        }

        return Optional.empty();
    }
}