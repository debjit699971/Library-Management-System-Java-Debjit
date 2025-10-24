package com.knf.dev.librarymanagementsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.knf.dev.librarymanagementsystem.service.BorrowService;

@Controller
@RequestMapping("/borrow")
public class BorrowController {

    @Autowired
    private BorrowService borrowService;

    @GetMapping("/issue")
    public String showIssueForm() {
        return "issue_book"; // will create this HTML next
    }

    @PostMapping("/issue")
    public String issueBook(@RequestParam Long userId, @RequestParam Long bookId, Model model) {
        try {
            borrowService.issueBook(userId, bookId);
            model.addAttribute("message", "Book issued successfully!");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "issue_book";
    }

    @GetMapping("/return")
    public String showReturnForm() {
        return "return_book";
    }

    @PostMapping("/return")
    public String returnBook(@RequestParam Long borrowId, Model model) {
        try {
            borrowService.returnBook(borrowId);
            model.addAttribute("message", "Book returned successfully!");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "return_book";
    }
}
