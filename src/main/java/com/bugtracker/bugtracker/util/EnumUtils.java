package com.bugtracker.bugtracker.util;

import com.bugtracker.bugtracker.model.enums.BugPriority;
import com.bugtracker.bugtracker.model.enums.BugStatus;
import com.bugtracker.bugtracker.model.enums.LogLevel;
import com.bugtracker.bugtracker.model.enums.ProjectType;


public class EnumUtils {

    private EnumUtils() {
   
    public static boolean canTransitionTo(BugStatus currentStatus, BugStatus targetStatus) {
        return currentStatus.canTransitionTo(targetStatus);
    }
    

    public static int getRecommendedResolutionTimeInHours(BugPriority priority) {
        return priority.getRecommendedResolutionTimeInHours();
    }
    

    public static String getLogLevelCssClass(LogLevel level) {
        return level.getCssClass();
    }
    
 
    public static String getProjectTypeDisplayName(ProjectType type) {
        return type.getDisplayName();
    }
} 
