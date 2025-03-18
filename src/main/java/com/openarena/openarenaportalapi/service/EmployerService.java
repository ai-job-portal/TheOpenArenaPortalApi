// EmployerService.java
package com.openarena.openarenaportalapi.service;

import com.openarena.openarenaportalapi.dto.DashboardOverviewDTO;
import com.openarena.openarenaportalapi.model.Job;
import com.openarena.openarenaportalapi.model.Recruiter;
import com.openarena.openarenaportalapi.model.User;
import com.openarena.openarenaportalapi.repository.JobRepository;
import com.openarena.openarenaportalapi.repository.RecruiterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EmployerService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private RecruiterRepository recruiterRepository; // Keep UserRepository

    public DashboardOverviewDTO getDashboardOverview() {
        // Get the currently authenticated user's details
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Recruiter> user = recruiterRepository.findByUsername(username);

        if (user.isPresent()) {
            Integer employerId = user.get().getId();

            // Fetch data using the repositories
            List<Job> activeJobs = jobRepository.findActiveJobsByEmployerId(employerId);
            List<Job> recentJobs = jobRepository.findRecentJobs(employerId, LocalDateTime.now().minusDays(7)); // Last 7 days
            List<Job> expiringJobs = jobRepository.findExpiringJobs(employerId, LocalDateTime.now(), LocalDateTime.now().plusDays(7)); // Expiring in next 7 days

            // Placeholder values, replace with actual logic when you have messages/pending actions
            int unreadMessagesCount = 0;
            int pendingActionsCount = 0;

            // Create and return the DTO
            return new DashboardOverviewDTO(activeJobs.size(), recentJobs, expiringJobs, unreadMessagesCount, pendingActionsCount);

        } else {
            throw new RuntimeException("User not found or not an employer");
        }
    }

    public User getEmployerById(Integer employerId) {
        return recruiterRepository.findById(employerId)
                .orElseThrow(() -> new RuntimeException("Employer not found with id: " + employerId));
    }
}