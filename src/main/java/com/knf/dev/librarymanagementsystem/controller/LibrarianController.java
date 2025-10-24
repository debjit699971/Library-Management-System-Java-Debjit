package com.knf.dev.librarymanagementsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.knf.dev.librarymanagementsystem.entity.Role;
import com.knf.dev.librarymanagementsystem.entity.User;
import com.knf.dev.librarymanagementsystem.repository.RoleRepository;
import com.knf.dev.librarymanagementsystem.repository.UserRepository;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/librarian")
public class LibrarianController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // ✅ Librarian Dashboard
    @GetMapping("/dashboard")
    public String showLibrarianDashboard(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = userRepo.findByEmail(principal.getName());
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // ✅ Ensure only librarians can access this
        boolean isLibrarian = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_LIBRARIAN"));
        if (!isLibrarian) {
            return "redirect:/access-denied";
        }

        model.addAttribute("user", user);
        return "librarian-dashboard";
    }

    // ✅ Manage Members Page
    @GetMapping("/members")
    public String manageMembers(Model model) {
        var members = userRepo.findAll().stream()
                .filter(u -> u.getRoles().stream()
                        .anyMatch(r -> r.getName().equals("ROLE_MEMBER")))
                .toList();

        model.addAttribute("members", members);
        model.addAttribute("newMember", new User()); // for the add-member form
        return "librarian-manage-members";
    }

    // ✅ Add new member
    @PostMapping("/members/add")
    public String addMember(@ModelAttribute("newMember") User newMember) {
        // Check if email already exists
        if (userRepo.findByEmail(newMember.getEmail()) != null) {
            return "redirect:/librarian/members?error=email_exists";
        }

        Role memberRole = roleRepo.findByName("ROLE_MEMBER")
                .orElseGet(() -> roleRepo.save(new Role("ROLE_MEMBER")));

        newMember.setPassword(passwordEncoder.encode(newMember.getPassword()));
        newMember.setRoles(List.of(memberRole));
        userRepo.save(newMember);

        return "redirect:/librarian/members?success";
    }

    // ✅ Remove member
   @GetMapping("/remove-member/{id}")
public String removeMember(@PathVariable Long id) {
    User user = userRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));

    // ✅ Ensure only members can be deleted
    boolean isMember = user.getRoles().stream()
            .anyMatch(role -> role.getName().equals("ROLE_MEMBER"));
    if (!isMember) {
        throw new RuntimeException("Only members can be deleted by librarian.");
    }

    // ✅ Unlink roles before deleting to avoid constraint violation
    user.getRoles().clear();
    userRepo.save(user);

    // ✅ Now safely delete the user
    userRepo.deleteById(id);

    return "redirect:/librarian/members?deleted";
}

}
