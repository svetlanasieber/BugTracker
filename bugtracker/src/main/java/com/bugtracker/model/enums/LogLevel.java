package com.bugtracker.model.enums;

public enum LogLevel {

    DEBUG("text-secondary"),

    INFO("text-info"),
    WARNING("text-warning"),
    ERROR("text-danger"),
    CRITICAL("bg-danger text-white");
    private final String cssClass;
    LogLevel(String cssClass) {
        this.cssClass = cssClass;
    }
    public String getCssClass() {
        return cssClass;
    }
} 