package com.knf.dev.librarymanagementsystem.service;

public interface BorrowService {
    void issueBook(Long userId, Long bookId) throws Exception;
    void returnBook(Long bookId) throws Exception;
}
