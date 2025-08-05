package com.bugtracker.bugtracker.util;

import com.bugtracker.bugtracker.model.enums.BugPriority;
import com.bugtracker.bugtracker.model.enums.BugStatus;
import com.bugtracker.bugtracker.model.enums.LogLevel;
import com.bugtracker.bugtracker.model.enums.ProjectType;

/**
 * Utility class with helper methods for working with enums in the system.
 * This class delegates to the specific enum methods.
 */
public class EnumUtils {

    private EnumUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Checks if a status transition is valid
     * 
     * @param currentStatus the current status
     * @param targetStatus the target status
     * @return true if the transition is valid, false otherwise
     */
    public static boolean canTransitionTo(BugStatus currentStatus, BugStatus targetStatus) {
        return currentStatus.canTransitionTo(targetStatus);
    }
    
    /**
     * Gets the recommended resolution time in hours based on bug priority
     * 
     * @param priority the bug priority
     * @return the recommended time to resolve in hours
     */
    public static int getRecommendedResolutionTimeInHours(BugPriority priority) {
        return priority.getRecommendedResolutionTimeInHours();
    }
    
    /**
     * Gets the CSS class for displaying a log level
     * 
     * @param level the log level
     * @return the appropriate CSS class name
     */
    public static String getLogLevelCssClass(LogLevel level) {
        return level.getCssClass();
    }
    
    /**
     * Gets a user-friendly display name for a project type
     * 
     * @param type the project type
     * @return a display name for the project type
     */
    public static String getProjectTypeDisplayName(ProjectType type) {
        return type.getDisplayName();
    }
} 