package com.bugtracker.bugtracker.service;

import com.bugtracker.bugtracker.bug.model.Bug;
import com.bugtracker.bugtracker.model.enums.BugStatus;
import com.bugtracker.bugtracker.model.enums.LogLevel;
import com.bugtracker.bugtracker.bug.repository.BugRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BugSchedulingService {

    private static final Logger log = LoggerFactory.getLogger(BugSchedulingService.class);
    
    private final BugRepository bugRepository;
    private final LogService logService;
    
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void closeInactiveBugs() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        
       
        List<Bug> inactiveBugs = bugRepository.findByUpdatedAtBeforeAndStatusNot(
                thirtyDaysAgo, 
                BugStatus.CLOSED
        );
        
        for (Bug bug : inactiveBugs) {
            bug.setStatus(BugStatus.CLOSED);
            bug.setClosedAt(LocalDateTime.now());
            bug.setUpdatedAt(LocalDateTime.now());
            bugRepository.save(bug);
            
       
            logService.createLogEntry(
                    "AUTO_CLOSE",
                    "Bug",
                    bug.getId(),
                    null,
                    "Bug automatically closed due to inactivity for 30+ days",
                    LogLevel.INFO
            );
            
            log.info("Automatically closed bug #{} due to inactivity", bug.getId());
        }
        
        log.info("Closed {} inactive bugs", inactiveBugs.size());
    }
} 
