package com.bugtracker.scheduler;

import com.bugtracker.bug.model.Bug;
import com.bugtracker.model.enums.BugStatus;
import com.bugtracker.model.enums.LogLevel;
import com.bugtracker.bug.service.BugService;
import com.bugtracker.service.LogService;

import java.time.LocalDateTime;
import java.util.List;

public class BugAutoCloseScheduler {

    private final BugService bugService;
    private final LogService logService;
    public BugAutoCloseScheduler(BugService bugService, LogService logService) {
        this.bugService = bugService;
        this.logService = logService;
    }
    public void autoCloseStaleBugs() {
        System.out.println("Starting scheduled task: Auto-closing stale bugs");
        try {
            List<Bug> staleBugs = bugService.findBugsNotUpdatedSince(LocalDateTime.now().minusDays(30));
            for (Bug bug : staleBugs) {
                if (bug.getStatus() != BugStatus.CLOSED && bug.getStatus() != BugStatus.RESOLVED) {
                    System.out.println("Auto-closing stale bug: " + bug.getId());
                    bugService.changeBugStatus(bug.getId(), BugStatus.CLOSED);
                    logService.createLogEntry(
                            "SYSTEM_AUTO_CLOSE", 
                            "Bug", 
                            bug.getId(),
                            0L,
                            "Bug auto-closed due to inactivity",
                            LogLevel.INFO
                    );
                }
            }
            System.out.println("Completed auto-closing " + staleBugs.size() + " stale bugs");
        } catch (Exception e) {
            System.err.println("Error during auto-close of stale bugs: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 