package com.knf.dev.librarymanagementsystem.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.knf.dev.librarymanagementsystem.entity.Book;
import com.knf.dev.librarymanagementsystem.entity.BorrowRecord;
import com.knf.dev.librarymanagementsystem.entity.User;  // ✅ Make sure this import is here

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    boolean existsByBookAndReturnDateIsNull(Book book);
    List<BorrowRecord> findAllByBook(Book book);
    List<BorrowRecord> findAllByBookAndReturnDateIsNull(Book book);
    List<BorrowRecord> findAllByUser(User user);  // ✅ this should now resolve cleanly
}
