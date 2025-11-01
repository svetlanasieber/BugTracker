package com.bugtracker.model.enums;

public enum ProjectType {
    
    SOFTWARE("Software Development"),
    HARDWARE("Hardware Development"),
    MOBILE("Mobile Application"),
    WEB("Web Application"),
    DESKTOP("Desktop Application"),
    INTEGRATION("Integration Project"),
    RESEARCH("Research & Development"),
    OTHER("Other");
    
    private final String displayName;
    
    ProjectType(String displayName) {
        this.displayName = displayName;
    }
    
    
    public String getDisplayName() {
        return displayName;
    }
} 
