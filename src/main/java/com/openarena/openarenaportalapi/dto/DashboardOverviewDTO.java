// DashboardOverviewDTO.java
package com.openarena.openarenaportalapi.dto;

import com.openarena.openarenaportalapi.model.Job;
import java.util.List;

public class DashboardOverviewDTO {

    private int totalActiveJobs;
    private List<Job> recentJobs;
    private List<Job> expiringJobs;
    private int unreadMessagesCount; // Keep this for future use
    private int pendingActionsCount; // Keep this for future use

    // Constructors
    public DashboardOverviewDTO() {}

    public DashboardOverviewDTO(int totalActiveJobs,
                                List<Job> recentJobs, List<Job> expiringJobs,
                                int unreadMessagesCount, int pendingActionsCount) {
        this.totalActiveJobs = totalActiveJobs;
        this.recentJobs = recentJobs;
        this.expiringJobs = expiringJobs;
        this.unreadMessagesCount = unreadMessagesCount;
        this.pendingActionsCount = pendingActionsCount;
    }

    // Getters and setters (essential!)

    public int getTotalActiveJobs() {
        return totalActiveJobs;
    }

    public void setTotalActiveJobs(int totalActiveJobs) {
        this.totalActiveJobs = totalActiveJobs;
    }

    public List<Job> getRecentJobs() {
        return recentJobs;
    }

    public void setRecentJobs(List<Job> recentJobs) {
        this.recentJobs = recentJobs;
    }

    public List<Job> getExpiringJobs() {
        return expiringJobs;
    }

    public void setExpiringJobs(List<Job> expiringJobs) {
        this.expiringJobs = expiringJobs;
    }

    public int getUnreadMessagesCount() {
        return unreadMessagesCount;
    }

    public void setUnreadMessagesCount(int unreadMessagesCount) {
        this.unreadMessagesCount = unreadMessagesCount;
    }

    public int getPendingActionsCount() {
        return pendingActionsCount;
    }

    public void setPendingActionsCount(int pendingActionsCount) {
        this.pendingActionsCount = pendingActionsCount;
    }
}