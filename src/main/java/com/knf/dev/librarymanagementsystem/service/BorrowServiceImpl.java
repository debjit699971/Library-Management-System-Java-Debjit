package com.knf.dev.librarymanagementsystem.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.knf.dev.librarymanagementsystem.entity.Book;
import com.knf.dev.librarymanagementsystem.entity.BorrowRecord;
import com.knf.dev.librarymanagementsystem.entity.User;
import com.knf.dev.librarymanagementsystem.repository.BookRepository;
import com.knf.dev.librarymanagementsystem.repository.BorrowRecordRepository;
import com.knf.dev.librarymanagementsystem.repository.UserRepository;

@Service
public class BorrowServiceImpl implements BorrowService {

    @Autowired
    private BorrowRecordRepository borrowRepo;

    @Autowired
    private BookRepository bookRepo;

    @Autowired
    private UserRepository userRepo;

    @Override
public void issueBook(Long userId, Long bookId) throws Exception {
    User user = userRepo.findById(userId)
            .orElseThrow(() -> new Exception("User not found"));
    Book book = bookRepo.findById(bookId)
            .orElseThrow(() -> new Exception("Book not found"));

    // ✅ Only allow members to borrow
    boolean isMember = user.getRoles().stream()
            .anyMatch(role -> role.getName().equalsIgnoreCase("ROLE_MEMBER"));
    if (!isMember) {
        throw new Exception("Only members can borrow books!");
    }

    // ✅ Prevent issuing if already borrowed
    if (!book.isAvailable()) {
        throw new Exception("Book is already borrowed!");
    }

    // ✅ Prevent multiple active borrow records for same book
    if (borrowRepo.existsByBookAndReturnDateIsNull(book)) {
        throw new Exception("This book is already issued and not yet returned!");
    }

    // ✅ Proceed with issuing
    BorrowRecord record = new BorrowRecord();
    record.setUser(user);
    record.setBook(book);
    record.setIssueDate(LocalDate.now());
    record.setDueDate(LocalDate.now().plusDays(14));

    borrowRepo.save(record);

    book.setAvailable(false);
    bookRepo.save(book);
}


    @Override
    public void returnBook(Long bookId) throws Exception {
        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new Exception("Book not found"));

        if (book.isAvailable()) {
            throw new Exception("Book was not issued!");
        }

        List<BorrowRecord> records = borrowRepo.findAllByBookAndReturnDateIsNull(book);
        if (records.isEmpty()) {
            throw new Exception("No active borrow record found for this book!");
        }

        for (BorrowRecord record : records) {
            record.setReturnDate(LocalDate.now());
            borrowRepo.save(record);
        }

        book.setAvailable(true);
        bookRepo.save(book);
    }
}

