package com.bugtracker.bugtracker.model.enums;

public enum BugPriority {

    LOW(120),      // 5 days
    MEDIUM(48),    // 2 days
    HIGH(24),      // 1 day
    CRITICAL(4);   // 4 hours

    private final int recommendedHours;

    BugPriority(int recommendedHours) {
        this.recommendedHours = recommendedHours;
    }

    public int getRecommendedResolutionTimeInHours() {
        return recommendedHours;
    }
} 