package com.bugtracker.bugtracker.web.controller;

import com.bugtracker.bugtracker.bug.model.Bug;
import com.bugtracker.bugtracker.user.model.User;
import com.bugtracker.bugtracker.model.enums.BugPriority;
import com.bugtracker.bugtracker.model.enums.BugStatus;
import com.bugtracker.bugtracker.bug.service.BugService;
import com.bugtracker.bugtracker.project.service.ProjectService;
import com.bugtracker.bugtracker.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;
    private final ProjectService projectService;
    private final BugService bugService;

    @GetMapping("/")
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        // Check if user is authenticated (not anonymous)
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            User user = userService.findByUsername(auth.getName());
            model.addAttribute("user", user);
            
            // Add statistics for dashboard
            model.addAttribute("totalProjects", projectService.countUserProjects(user.getId()));
            model.addAttribute("assignedBugs", bugService.countAssignedBugs(user.getId()));
            model.addAttribute("reportedBugs", bugService.countReportedBugs(user.getId()));
            
            // Get recent bugs
            List<Bug> recentBugs = bugService.findRecentBugsByUser(user.getId(), 5);
            model.addAttribute("recentBugs", recentBugs);
            
            // Add bug status counts
            Map<BugStatus, Long> bugStatusCounts = calculateBugStatusCounts(user.getId());
            model.addAttribute("bugStatusCounts", bugStatusCounts);
            
            // Add bug priority counts
            Map<BugPriority, Long> bugPriorityCounts = calculateBugPriorityCounts(user.getId());
            model.addAttribute("bugPriorityCounts", bugPriorityCounts);
            
            return "dashboard";
        }
        
        // Show landing page for not logged in users
        return "landing";
    }

    @GetMapping("/landing")
    public String landing() {
        // Always show landing page regardless of authentication status
        return "landing";
    }
    
    private Map<BugStatus, Long> calculateBugStatusCounts(Long userId) {
        Map<BugStatus, Long> statusCounts = new HashMap<>();
        
        // Get total counts for each status
        for (BugStatus status : BugStatus.values()) {
            long count = bugService.countBugsByStatus(status, userId);
            statusCounts.put(status, count);
        }
        
        return statusCounts;
    }
    
    private Map<BugPriority, Long> calculateBugPriorityCounts(Long userId) {
        Map<BugPriority, Long> priorityCounts = new HashMap<>();
        
        // Get total counts for each priority
        for (BugPriority priority : BugPriority.values()) {
            long count = bugService.countBugsByPriority(priority, userId);
            priorityCounts.put(priority, count);
        }
        
        return priorityCounts;
    }
} 