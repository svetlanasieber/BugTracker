package com.bugtracker.web.controller;

import com.bugtracker.web.dto.BugAdd;
import com.bugtracker.web.dto.BugUpdate;
import com.bugtracker.bug.model.Bug;
import com.bugtracker.comment.model.Comment;
import com.bugtracker.model.enums.BugPriority;
import com.bugtracker.model.enums.BugStatus;
import com.bugtracker.bug.service.BugService;
import com.bugtracker.comment.service.CommentService;
import com.bugtracker.project.service.ProjectService;
import com.bugtracker.user.service.UserService;
import com.bugtracker.validation.groups.OnCreate;
import com.bugtracker.validation.groups.OnUpdate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/bugs")
@RequiredArgsConstructor
public class BugController {

    private final BugService bugService;
    private final ProjectService projectService;
    private final UserService userService;
    private final CommentService commentService;

    @GetMapping
    public String listBugs(
            @RequestParam(required = false) UUID projectId,
            @RequestParam(required = false) BugStatus status,
            @RequestParam(required = false) BugPriority priority,
            Model model) {
        List<Bug> bugs = bugService.findBugsWithFilters(projectId, status, priority);
        if (projectId != null) {
            projectService.findById(projectId).ifPresent(project -> 
                model.addAttribute("currentProject", project));
        }
        model.addAttribute("bugs", bugs);
        loadFilterData(model);
        return "bugs/list";
    }

    @GetMapping("/{id}")
    public String viewBug(@PathVariable UUID id, Model model) {
        Optional<Bug> bugOpt = bugService.findById(id);
        if (bugOpt.isEmpty()) {
            return "redirect:/bugs?error=Bug+not+found";
        }
        Bug bug = bugOpt.get();
        List<Comment> comments = commentService.getCommentsByBugId(id);
        model.addAttribute("bug", bug);
        model.addAttribute("comments", comments);
        loadFormData(model);
        return "bugs/details";
    }

    @GetMapping("/create")
    public String showCreateForm(
            @RequestParam(required = false) UUID projectId,
            Model model) {
        BugAdd bugAdd = new BugAdd();
        bugAdd.setPriority(BugPriority.MEDIUM);
        if (projectId != null) {
            bugAdd.setProjectId(projectId);
        }
        model.addAttribute("bugAdd", bugAdd);
        loadFormData(model);
        return "bugs/create";
    }

    @PostMapping("/create")
    public String createBug(
            @Validated(OnCreate.class) @ModelAttribute("bugAdd") BugAdd bugAdd,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (bindingResult.hasErrors()) {
            loadFormData(model);
            return "bugs/create";
        }
        Bug savedBug = bugService.createBugFromDTOWithCurrentUser(bugAdd);
        redirectAttributes.addFlashAttribute("success", "Bug created successfully!");
        return "redirect:/bugs/" + savedBug.getId();
    }

    @PostMapping("/{id}/update")
    public String updateBug(
            @PathVariable UUID id,
            @Validated(OnUpdate.class) @ModelAttribute("bugUpdate") BugUpdate bugUpdate,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (bindingResult.hasErrors()) {
            loadFormData(model);
            return "bugs/details";
        }
        bugUpdate.setId(id);
        bugService.updateBugFromDTO(bugUpdate);
        redirectAttributes.addFlashAttribute("success", "Bug updated successfully!");
        return "redirect:/bugs/" + id;
    }

    @PostMapping("/{id}/assign")
    public String assignBug(
            @PathVariable UUID id,
            @RequestParam(required = false) Long userId,
            RedirectAttributes redirectAttributes) {
        String message = bugService.assignBugWithMessage(id, userId);
        redirectAttributes.addFlashAttribute("success", message);
        return "redirect:/bugs/" + id;
    }

    @PostMapping("/{id}/status")
    public String changeBugStatus(
            @PathVariable UUID id,
            @RequestParam BugStatus status,
            RedirectAttributes redirectAttributes) {
        bugService.changeBugStatus(id, status);
        redirectAttributes.addFlashAttribute("success", "Bug status updated successfully!");
        return "redirect:/bugs/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteBug(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes) {
        bugService.deleteBug(id);
        redirectAttributes.addFlashAttribute("success", "Bug deleted successfully!");
        return "redirect:/bugs";
    }

    private void loadFormData(Model model) {
        model.addAttribute("projects", projectService.findAll());
        model.addAttribute("statuses", BugStatus.values());
        model.addAttribute("priorities", BugPriority.values());
        model.addAttribute("users", userService.findAllUsers());
    }

    private void loadFilterData(Model model) {
        model.addAttribute("projects", projectService.findAll());
        model.addAttribute("statuses", BugStatus.values());
        model.addAttribute("priorities", BugPriority.values());
    }
}
