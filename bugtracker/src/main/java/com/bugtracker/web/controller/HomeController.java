package com.bugtracker.web.controller;

import com.bugtracker.bug.model.Bug;
import com.bugtracker.user.model.User;
import com.bugtracker.model.enums.BugPriority;
import com.bugtracker.model.enums.BugStatus;
import com.bugtracker.bug.service.BugService;
import com.bugtracker.project.service.ProjectService;
import com.bugtracker.user.service.UserService;
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
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            User user = userService.findByUsername(auth.getName());
            model.addAttribute("user", user);
            model.addAttribute("totalProjects", projectService.countUserProjects(user.getId()));
            model.addAttribute("assignedBugs", bugService.countAssignedBugs(user.getId()));
            model.addAttribute("reportedBugs", bugService.countReportedBugs(user.getId()));
            List<Bug> recentBugs = bugService.findRecentBugsByUser(user.getId(), 5);
            model.addAttribute("recentBugs", recentBugs);
            Map<BugStatus, Long> bugStatusCounts = calculateBugStatusCounts(user.getId());
            model.addAttribute("bugStatusCounts", bugStatusCounts);
            Map<BugPriority, Long> bugPriorityCounts = calculateBugPriorityCounts(user.getId());
            model.addAttribute("bugPriorityCounts", bugPriorityCounts);
            return "dashboard";
        }
        return "landing";
    }

    @GetMapping("/landing")
    public String landing() {
        return "landing";
    }
    private Map<BugStatus, Long> calculateBugStatusCounts(Long userId) {
        Map<BugStatus, Long> statusCounts = new HashMap<>();
        for (BugStatus status : BugStatus.values()) {
            long count = bugService.countBugsByStatus(status, userId);
            statusCounts.put(status, count);
        }
        return statusCounts;
    }
    private Map<BugPriority, Long> calculateBugPriorityCounts(Long userId) {
        Map<BugPriority, Long> priorityCounts = new HashMap<>();
        for (BugPriority priority : BugPriority.values()) {
            long count = bugService.countBugsByPriority(priority, userId);
            priorityCounts.put(priority, count);
        }
        return priorityCounts;
    }
} 