package com.bugtracker.web.controller;

import com.bugtracker.web.dto.PasswordChange;
import com.bugtracker.web.dto.ProfileEdit;
import com.bugtracker.user.model.User;
import com.bugtracker.bug.service.BugService;
import com.bugtracker.user.service.UserService;
import com.bugtracker.validation.groups.OnPasswordChange;
import com.bugtracker.validation.groups.OnUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final BugService bugService;
    @GetMapping
    public String showProfile(Principal principal, Model model) {
        User currentUser = userService.getCurrentUserWithStats(principal.getName());
        Long reportedBugsCount = bugService.countReportedBugs(currentUser.getId());
        Long assignedBugsCount = bugService.countAssignedBugs(currentUser.getId());
        model.addAttribute("user", currentUser);
        model.addAttribute("reportedBugsCount", reportedBugsCount);
        model.addAttribute("assignedBugsCount", assignedBugsCount);
        model.addAttribute("profileEdit", new ProfileEdit());
        model.addAttribute("passwordChange", new PasswordChange());
        return "profile/profile";
    }
    @PostMapping("/edit")
    public String updateProfile(@Validated(OnUpdate.class) @ModelAttribute("profileEdit") ProfileEdit profileEdit,
                               BindingResult bindingResult,
                               Principal principal,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (bindingResult.hasErrors()) {
            User currentUser = userService.getCurrentUserWithStats(principal.getName());
            Long reportedBugsCount = bugService.countReportedBugs(currentUser.getId());
            Long assignedBugsCount = bugService.countAssignedBugs(currentUser.getId());
            model.addAttribute("user", currentUser);
            model.addAttribute("reportedBugsCount", reportedBugsCount);
            model.addAttribute("assignedBugsCount", assignedBugsCount);
            model.addAttribute("passwordChange", new PasswordChange());
            return "profile/profile";
        }
        userService.updateUserProfile(principal.getName(), profileEdit);
        redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        return "redirect:/profile";
    }
    @PostMapping("/change-password")
    public String changePassword(@Validated(OnPasswordChange.class) @ModelAttribute("passwordChange") PasswordChange passwordChange,
                                BindingResult bindingResult,
                                Principal principal,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (bindingResult.hasErrors()) {
            User currentUser = userService.getCurrentUserWithStats(principal.getName());
            Long reportedBugsCount = bugService.countReportedBugs(currentUser.getId());
            Long assignedBugsCount = bugService.countAssignedBugs(currentUser.getId());
            model.addAttribute("user", currentUser);
            model.addAttribute("reportedBugsCount", reportedBugsCount);
            model.addAttribute("assignedBugsCount", assignedBugsCount);
            model.addAttribute("profileEdit", new ProfileEdit());
            return "profile/profile";
        }
        boolean success = userService.changeUserPassword(principal.getName(), passwordChange);
        if (success) {
            redirectAttributes.addFlashAttribute("success", "Password changed successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Current password is incorrect!");
        }
        return "redirect:/profile";
    }
} 