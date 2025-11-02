package com.bugtracker.model.enums;

public enum TeamRole {
    DEVELOPER("Developer"),
    QA_ENGINEER("QA Engineer"),
    PROJECT_MANAGER("Project Manager"),
    BASIC_USER("Basic User");

    private final String displayName;

    TeamRole(String displayName) {
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

