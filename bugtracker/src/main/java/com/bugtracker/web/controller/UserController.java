package com.bugtracker.web.controller;

import com.bugtracker.web.dto.UserRegister;
import com.bugtracker.user.service.UserService;
import com.bugtracker.validation.groups.OnCreate;
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

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    
    
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("userRegister", new UserRegister());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Validated(OnCreate.class) @ModelAttribute UserRegister userRegister, 
                             BindingResult bindingResult, 
                             Model model, 
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("userRegister", userRegister);
            return "auth/register";
        }

        try {
            userService.createUser(userRegister);
            redirectAttributes.addFlashAttribute("registrationSuccess", 
                "Registration successful! You can now log in with your credentials.");
            return "redirect:/auth/login";
        } catch (RuntimeException e) {
            model.addAttribute("registrationError", e.getMessage());
            model.addAttribute("userRegister", userRegister);
            return "auth/register";
        }
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "auth/login";
    }
}
