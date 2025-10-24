package com.knf.dev.librarymanagementsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.knf.dev.librarymanagementsystem.entity.Role;
import com.knf.dev.librarymanagementsystem.entity.User;
import com.knf.dev.librarymanagementsystem.repository.RoleRepository;
import com.knf.dev.librarymanagementsystem.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private UserRepository userRepo;
    @Autowired private RoleRepository roleRepo;
    @Autowired private BCryptPasswordEncoder passwordEncoder;

    // Existing admin dashboard links
    @GetMapping("/reports")
    public String showReportsPage() {
        return "reports";
    }

    // --- User Management ---
    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userRepo.findAll());
        return "admin-users";
    }

    @GetMapping("/users/add")
    public String addUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleRepo.findAll());
        return "admin-add-user";
    }

    @PostMapping("/users/save")
    public String saveUser(@ModelAttribute("user") User user, @RequestParam("roleId") Long roleId) {
        Role role = roleRepo.findById(roleId).orElseThrow();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Arrays.asList(role));
        userRepo.save(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/delete-user/{id}")
public String deleteUser(@PathVariable Long id) {
    User user = userRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));

    // ⛔ Prevent deleting admins
    if (user.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_ADMIN"))) {
        throw new RuntimeException("Admin users cannot be deleted.");
    }

    // ✅ Unlink roles
    user.getRoles().clear();
    userRepo.save(user);

    // ✅ Delete user
    userRepo.deleteById(id);

    return "redirect:/admin/users?deleted";
}



    @GetMapping("/dashboard")
    public String adminDashboard() {
    return "admin-dashboard";
}

}

