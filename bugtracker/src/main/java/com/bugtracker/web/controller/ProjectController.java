package com.bugtracker.web.controller;

import com.bugtracker.project.model.Project;
import com.bugtracker.user.model.User;
import com.bugtracker.web.dto.ProjectAdd;
import com.bugtracker.web.dto.ProjectUpdate;
import com.bugtracker.project.service.ProjectService;
import com.bugtracker.user.service.UserService;
import com.bugtracker.validation.groups.OnCreate;
import com.bugtracker.validation.groups.OnUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/projects")
@RequiredArgsConstructor
@Slf4j
public class ProjectController {

    private final ProjectService projectService;
    private final UserService userService;

    @GetMapping
    public String listProjects(Model model, Principal principal) {
        List<Project> projects = projectService.getProjectsForUser(principal.getName());
        boolean isAdmin = userService.isUserAdmin(principal.getName());
        model.addAttribute("projects", projects);
        model.addAttribute("isAdmin", isAdmin);
        return "projects/list";
    }

    @GetMapping("/{id}")
    public String viewProject(@PathVariable UUID id, Model model, Principal principal) {
        if (!projectService.isUserAuthorizedForProject(principal.getName(), id)) {
            return "redirect:/access-denied";
        }
        Project project = projectService.getProjectById(id);
        model.addAttribute("project", project);
        model.addAttribute("bugs", project.getBugs());
        model.addAttribute("users", project.getMembers());
        model.addAttribute("allUsers", userService.findAll());
        return "projects/view";
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCreateProjectForm(Model model) {
        ProjectAdd projectAdd = new ProjectAdd();
        // Set default values
        projectAdd.setActive(true);
        model.addAttribute("projectAdd", projectAdd);
        loadFormData(model);
        return "projects/create";
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String createProject(@Validated(OnCreate.class) @ModelAttribute("projectAdd") ProjectAdd projectAdd,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (bindingResult.hasErrors()) {
            loadFormData(model);
            return "projects/create";
        }
        
        try {
            Project project = projectService.createProjectFromDTO(projectAdd);
            redirectAttributes.addFlashAttribute("success", "Project created successfully");
            return "redirect:/projects/" + project.getId();
        } catch (Exception e) {
            log.error("Error creating project: {}", e.getMessage(), e);
            model.addAttribute("error", "Failed to create project: " + e.getMessage());
            loadFormData(model);
            return "projects/create";
        }
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditProjectForm(@PathVariable UUID id, Model model) {
        Project project = projectService.getProjectById(id);
        ProjectUpdate projectUpdate = ProjectUpdate.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .projectType(project.getProjectType())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .isActive(project.isActive())
                .memberIds(project.getMembers().stream()
                        .map(User::getId)
                        .toList())
                .build();
        model.addAttribute("projectUpdate", projectUpdate);
        model.addAttribute("project", project); 
        loadFormData(model);
        return "projects/edit";
    }

    @PostMapping("/{id}/update")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateProject(@PathVariable UUID id,
                               @Validated(OnUpdate.class) @ModelAttribute("projectUpdate") ProjectUpdate projectUpdate,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (bindingResult.hasErrors()) {
            Project project = projectService.getProjectById(id);
            model.addAttribute("project", project); 
            loadFormData(model);
            return "projects/edit";
        }
        projectUpdate.setId(id); 
        projectService.updateProjectFromDTO(projectUpdate);
        redirectAttributes.addFlashAttribute("success", "Project updated successfully");
        return "redirect:/projects/" + id;
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteProject(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        projectService.deleteProject(id);
        redirectAttributes.addFlashAttribute("success", "Project deleted successfully");
        return "redirect:/projects";
    }
    @GetMapping("/fix-projects")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public String fixProjects() {
        return projectService.performProjectDatabaseFix();
    }
    private void loadFormData(Model model) {
        model.addAttribute("users", userService.findAll());
    }
} 