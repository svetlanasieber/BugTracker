package com.bugtracker.model.enums;

public enum BugPriority {

    LOW(120),      
    MEDIUM(48),    
    HIGH(24),      
    CRITICAL(4);   

    private final int recommendedHours;

    BugPriority(int recommendedHours) {
        this.recommendedHours = recommendedHours;
    }

    public int getRecommendedResolutionTimeInHours() {
        return recommendedHours;
    }
} 