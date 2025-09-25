package com.bugtracker.bugtracker.model.enums;

/**
 * Enum representing the possible types of projects in the system.
 */
public enum ProjectType {
    /**
     * Software development project
     */
    SOFTWARE("Software Development"),
    
    /**
     * Hardware development project
     */
    HARDWARE("Hardware Development"),
    
    /**
     * Mobile application project
     */
    MOBILE("Mobile Application"),
    
    /**
     * Web application project
     */
    WEB("Web Application"),
    
    /**
     * Desktop application project
     */
    DESKTOP("Desktop Application"),
    
    /**
     * Integration project
     */
    INTEGRATION("Integration Project"),
    
    /**
     * Research and development project
     */
    RESEARCH("Research & Development"),
    
    /**
     * Other type of project
     */
    OTHER("Other");
    
    private final String displayName;
    
    ProjectType(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * Returns a user-friendly name for the project type
     * 
     * @return a display name for this project type
     */
    public String getDisplayName() {
        return displayName;
    }
} 