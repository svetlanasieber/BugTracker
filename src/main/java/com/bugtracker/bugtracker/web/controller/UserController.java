package com.bugtracker.bugtracker.web.controller;

import com.bugtracker.bugtracker.web.dto.UserRegister;
import com.bugtracker.bugtracker.user.service.UserService;
import com.bugtracker.bugtracker.user.model.User;
import com.bugtracker.bugtracker.validation.groups.OnCreate;
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



    @GetMapping("/login")
    public String showLoginForm() {
        return "auth/login";
    }


}
