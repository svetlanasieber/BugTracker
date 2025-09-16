package com.bugtracker.bugtracker.web.controller;

import com.bugtracker.bugtracker.project.model.Project;
import com.bugtracker.bugtracker.user.model.User;
import com.bugtracker.bugtracker.web.dto.ProjectAdd;
import com.bugtracker.bugtracker.web.dto.ProjectUpdate;
import com.bugtracker.bugtracker.project.service.ProjectService;
import com.bugtracker.bugtracker.user.service.UserService;
import com.bugtracker.bugtracker.validation.groups.OnCreate;
import com.bugtracker.bugtracker.validation.groups.OnUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;


@Controller
@RequestMapping("/projects")
@RequiredArgsConstructor
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
    public String viewProject(@PathVariable Long id, Model model, Principal principal) {
     
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
        model.addAttribute("projectAdd", new ProjectAdd());
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
        
        Project project = projectService.createProjectFromDTO(projectAdd);
        redirectAttributes.addFlashAttribute("success", "Project created successfully");
        return "redirect:/projects/" + project.getId();
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditProjectForm(@PathVariable Long id, Model model) {
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
    public String updateProject(@PathVariable Long id,
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
    public String deleteProject(@PathVariable Long id, RedirectAttributes redirectAttributes) {
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
