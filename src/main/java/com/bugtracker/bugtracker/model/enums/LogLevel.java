package com.bugtracker.bugtracker.model.enums;

public enum LogLevel {

    DEBUG("text-secondary"),

    INFO("text-info"),
    
    /**
     * Warning level - indicates potential issues that don't prevent the system from working
     */
    WARNING("text-warning"),
    
    /**
     * Error level - serious issues that prevent features from working correctly
     */
    ERROR("text-danger"),
    
    /**
     * Critical level - critical issues that could cause system failure
     */
    CRITICAL("bg-danger text-white");
    
    private final String cssClass;
    
    LogLevel(String cssClass) {
        this.cssClass = cssClass;
    }
    
    /**
     * Returns the CSS class to use when displaying this log level
     * 
     * @return the appropriate CSS class name
     */
    public String getCssClass() {
        return cssClass;
    }
} 