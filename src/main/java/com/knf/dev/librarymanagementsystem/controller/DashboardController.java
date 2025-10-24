package com.knf.dev.librarymanagementsystem.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth) {
        for (GrantedAuthority authority : auth.getAuthorities()) {
            String role = authority.getAuthority();
            if (role.equals("ROLE_ADMIN")) {
                return "admin-dashboard";
            } else if (role.equals("ROLE_LIBRARIAN")) {
                return "librarian-dashboard";
            } else if (role.equals("ROLE_MEMBER")) {
                return "member-dashboard";
            }
        }
        return "redirect:/login?error";
    }
}
