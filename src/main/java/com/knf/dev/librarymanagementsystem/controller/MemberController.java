package com.knf.dev.librarymanagementsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.knf.dev.librarymanagementsystem.repository.*;
import com.knf.dev.librarymanagementsystem.entity.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private BorrowRecordRepository borrowRepo;

    @Autowired
    private BookRepository bookRepo;

    // ✅ Member Dashboard (Fix for your error)
   @GetMapping("/dashboard")
public String showMemberDashboard(Model model, Principal principal) {
    if (principal == null) {
        return "redirect:/login";
    }

    User user = userRepo.findByEmail(principal.getName());
    if (user == null) {
        throw new RuntimeException("User not found");
    }

    // ✅ Prevent admin/librarian from accessing this dashboard
    boolean isMember = user.getRoles().stream()
            .anyMatch(role -> role.getName().equals("ROLE_MEMBER"));
    if (!isMember) {
        return "redirect:/access-denied";
    }

    model.addAttribute("user", user);
    return "member-dashboard";
}




    // ✅ View all available books
    @GetMapping("/books")
    public String viewBooks(Model model) {
        model.addAttribute("books", bookRepo.findAll());
        return "member-books";
    }

    // ✅ View borrowed books for this member
    @GetMapping("/my-borrows/{userId}")
    public String viewMyBorrowedBooks(@PathVariable Long userId, Model model) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<BorrowRecord> borrowedBooks = borrowRepo.findAllByUser(user);

        model.addAttribute("user", user);
        model.addAttribute("borrowedBooks", borrowedBooks);
        return "member-my-borrows";
    }

    @GetMapping("/return-book/{bookId}")
    public String returnBook(@PathVariable Long bookId, Principal principal) {
        User user = userRepo.findByEmail(principal.getName());
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        List<BorrowRecord> records = borrowRepo.findAllByUser(user)
                .stream()
                .filter(r -> r.getBook().getId().equals(bookId) && r.getReturnDate() == null)
                .toList();

        if (records.isEmpty()) {
            throw new RuntimeException("No active borrow record found for this book.");
        }

        for (BorrowRecord record : records) {
            record.setReturnDate(LocalDate.now());
            borrowRepo.save(record);

            Book book = record.getBook();
            book.setAvailable(true);
            bookRepo.save(book);
        }

        return "redirect:/member/my-borrows/" + user.getId() + "?returned";
    }

}


