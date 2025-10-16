package com.bugtracker.projectservice.enums;

public enum ProjectType {
    WEB_APPLICATION("Web Application"),
    MOBILE_APPLICATION("Mobile Application"),
    DESKTOP_APPLICATION("Desktop Application"),
    API_SERVICE("API Service"),
    LIBRARY("Library"),
    OTHER("Other");

    private final String displayName;

    ProjectType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
