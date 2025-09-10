package com.bugtracker.bugtracker.model.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public enum BugStatus {

    NEW,
    IN_PROGRESS,
    TESTING,
    RESOLVED,
    CLOSED;

    public boolean canTransitionTo(BugStatus targetStatus) {
        Set<BugStatus> allowedTransitions;

        switch (this) {
            case NEW:
                allowedTransitions = new HashSet<>(Arrays.asList(IN_PROGRESS, CLOSED));
                break;

            case IN_PROGRESS:
                allowedTransitions = new HashSet<>(Arrays.asList(TESTING, RESOLVED, NEW));
                break;

            case TESTING:
                allowedTransitions = new HashSet<>(Arrays.asList(RESOLVED, IN_PROGRESS, NEW));
                break;

            case RESOLVED:
                allowedTransitions = new HashSet<>(Arrays.asList(CLOSED, IN_PROGRESS, TESTING));
                break;

            case CLOSED:
                allowedTransitions = new HashSet<>(Arrays.asList(NEW, IN_PROGRESS));
                break;

            default:
                allowedTransitions = Collections.emptySet();
        }

        return allowedTransitions.contains(targetStatus);
    }
} 
