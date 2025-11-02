package com.bugtracker.service;

import com.bugtracker.bug.model.Bug;
import com.bugtracker.model.enums.BugStatus;
import com.bugtracker.model.enums.LogLevel;
import com.bugtracker.bug.repository.BugRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
    name = "app.scheduling.bug-cleanup.enabled", 
    havingValue = "true", 
    matchIfMissing = true
)
public class BugSchedulingService {
    private final BugRepository bugRepository;
    private final LogService logService;
    @Value("${app.scheduling.bug-cleanup.days-threshold:30}")
    private int daysThreshold;
    @Scheduled(cron = "${app.scheduling.bug-cleanup.cron:0 0 0 * * ?}")
    @Transactional
    public void closeInactiveBugs() {
        try {
            log.info("Starting bug cleanup task (threshold: {} days)", daysThreshold);
            LocalDateTime thresholdDate = LocalDateTime.now().minusDays(daysThreshold);
            List<Bug> inactiveBugs = bugRepository.findByUpdatedAtBeforeAndStatusNot(
                    thresholdDate, 
                    BugStatus.CLOSED
            );
            if (inactiveBugs.isEmpty()) {
                log.info("No inactive bugs found for cleanup");
                return;
            }
            int closedCount = 0;
            int failedCount = 0;
            for (Bug bug : inactiveBugs) {
                try {
                    bug.setStatus(BugStatus.CLOSED);
                    bug.setClosedAt(LocalDateTime.now());
                    bug.setUpdatedAt(LocalDateTime.now());
                    bugRepository.save(bug);
                    logService.createLogEntry(
                            "AUTO_CLOSE",
                            "Bug",
                            bug.getId(),
                            null,
                            String.format("Bug automatically closed due to inactivity for %d+ days", daysThreshold),
                            LogLevel.INFO
                    );
                    closedCount++;
                    log.debug("Automatically closed bug #{} due to inactivity", bug.getId());
                } catch (Exception e) {
                    failedCount++;
                    log.error("Failed to close bug #{}: {}", bug.getId(), e.getMessage(), e);
                }
            }
            log.info("Bug cleanup completed: {} closed, {} failed out of {} inactive bugs", 
                    closedCount, failedCount, inactiveBugs.size());
        } catch (Exception e) {
            log.error("Bug cleanup task failed with error: {}", e.getMessage(), e);
        }
    }
}