package com.bugtracker.bugtracker.web;

import com.bugtracker.bugtracker.exception.BugNotFoundException;
import com.bugtracker.bugtracker.exception.FileUploadException;
import com.bugtracker.bugtracker.exception.ProjectNotFoundException;
import com.bugtracker.bugtracker.exception.UserAlreadyExistsException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Set;


@ControllerAdvice
@Slf4j
public class ExceptionAdvice {

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleUserAlreadyExists(UserAlreadyExistsException ex, RedirectAttributes redirectAttributes) {
        log.warn("User registration failed: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("error", "Email address is already registered. Please use a different email.");
        return "redirect:/auth/register";
    }

    @ExceptionHandler(BugNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleBugNotFound(BugNotFoundException ex, RedirectAttributes redirectAttributes) {
        log.warn("Bug not found: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("error", "Bug not found.");
        return "redirect:/bugs";
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleProjectNotFound(ProjectNotFoundException ex, RedirectAttributes redirectAttributes) {
        log.warn("Project not found: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("error", "Project not found.");
        return "redirect:/projects";
    }

    @ExceptionHandler(FileUploadException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleFileUpload(FileUploadException ex, RedirectAttributes redirectAttributes) {
        log.error("File upload error: {}", ex.getMessage(), ex);
        redirectAttributes.addFlashAttribute("error", "File upload failed: " + ex.getMessage());
        return "redirect:/profile";
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDenied(AccessDeniedException ex, Model model) {
        log.warn("Access denied: {}", ex.getMessage());
        model.addAttribute("error", "You don't have permission to access this resource.");
        return "error/access-denied";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(NoResourceFoundException ex, Model model) {
        log.warn("Resource not found: {}", ex.getMessage());
        model.addAttribute("error", "The requested page was not found.");
        return "error/404";
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleValidationErrors(Exception ex, RedirectAttributes redirectAttributes) {
        StringBuilder errorMessage = new StringBuilder("Validation failed: ");
        
        if (ex instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException validEx = (MethodArgumentNotValidException) ex;
            for (FieldError error : validEx.getBindingResult().getFieldErrors()) {
                errorMessage.append(error.getDefaultMessage()).append("; ");
            }
        } else if (ex instanceof BindException) {
            BindException bindEx = (BindException) ex;
            for (FieldError error : bindEx.getBindingResult().getFieldErrors()) {
                errorMessage.append(error.getDefaultMessage()).append("; ");
            }
        }
        
        log.warn("Validation error: {}", errorMessage.toString());
        redirectAttributes.addFlashAttribute("error", errorMessage.toString());
        return "redirect:/";
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleConstraintViolation(ConstraintViolationException ex, RedirectAttributes redirectAttributes) {
        StringBuilder errorMessage = new StringBuilder("Validation failed: ");
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            errorMessage.append(violation.getMessage()).append("; ");
        }
        
        log.warn("Constraint violation: {}", errorMessage.toString());
        redirectAttributes.addFlashAttribute("error", errorMessage.toString());
        return "redirect:/";
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleServiceErrors(RuntimeException ex, RedirectAttributes redirectAttributes) {
        log.error("Service error: {}", ex.getMessage(), ex);
        
    
        String message = ex.getMessage();
        if (message != null) {
            if (message.contains("User not found")) {
                redirectAttributes.addFlashAttribute("error", "User not found.");
                return "redirect:/auth/login";
            } else if (message.contains("Project not found")) {
                redirectAttributes.addFlashAttribute("error", "Project not found.");
                return "redirect:/projects";
            } else if (message.contains("Bug not found")) {
                redirectAttributes.addFlashAttribute("error", "Bug not found.");
                return "redirect:/bugs";
            }
        }
        
        redirectAttributes.addFlashAttribute("error", "An error occurred while processing your request.");
        return "redirect:/";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericError(Exception ex, Model model) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        model.addAttribute("error", "An unexpected error occurred. Please try again.");
        return "error/500";
    }
} 
