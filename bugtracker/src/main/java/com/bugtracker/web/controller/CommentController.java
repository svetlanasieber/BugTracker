package com.bugtracker.web.controller;

import com.bugtracker.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/create")
    public String createComment(@RequestParam Long bugId,
                               @RequestParam String content,
                               @RequestParam(value = "screenshot", required = false) MultipartFile screenshot,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
        
        try {
            
            commentService.createCommentWithScreenshot(bugId, content, screenshot, principal.getName());
            redirectAttributes.addFlashAttribute("success", "Comment added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error adding comment: " + e.getMessage());
        }
        
        return "redirect:/bugs/" + bugId;
    }

    @PostMapping("/{id}/update")
    public String updateComment(@PathVariable Long id,
                               @RequestParam String content,
                               @RequestParam Long bugId,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
        
        
        commentService.updateCommentWithAuthorization(id, content, principal.getName());
        redirectAttributes.addFlashAttribute("success", "Comment updated successfully!");
        
        return "redirect:/bugs/" + bugId;
    }

    @PostMapping("/{id}/delete")
    public String deleteComment(@PathVariable Long id,
                               @RequestParam Long bugId,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
        
        
        commentService.deleteCommentWithAuthorization(id, principal.getName());
        redirectAttributes.addFlashAttribute("success", "Comment deleted successfully!");
        
        return "redirect:/bugs/" + bugId;
    }
} 