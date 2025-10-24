package com.knf.dev.librarymanagementsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import com.knf.dev.librarymanagementsystem.entity.User;
import com.knf.dev.librarymanagementsystem.repository.UserRepository;
import java.security.Principal;

@Controller
public class LoginRedirectController {

    @Autowired
    private UserRepository userRepo;

    @GetMapping("/default")
    public String defaultAfterLogin(Principal principal) {
        if (principal == null) return "redirect:/login";

        User user = userRepo.findByEmail(principal.getName());
        if (user == null) return "redirect:/login";

        if (user.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_ADMIN"))) {
            return "redirect:/admin/dashboard";
        } else if (user.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_LIBRARIAN"))) {
            return "redirect:/librarian/dashboard";
        } else if (user.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_MEMBER"))) {
            return "redirect:/member/dashboard";
        }

        return "redirect:/access-denied";
    }
}
