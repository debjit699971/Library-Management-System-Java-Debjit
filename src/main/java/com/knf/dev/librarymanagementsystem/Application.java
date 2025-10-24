package com.knf.dev.librarymanagementsystem;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.knf.dev.librarymanagementsystem.entity.Author;
import com.knf.dev.librarymanagementsystem.entity.Book;
import com.knf.dev.librarymanagementsystem.entity.Category;
import com.knf.dev.librarymanagementsystem.entity.Publisher;
import com.knf.dev.librarymanagementsystem.entity.Role;
import com.knf.dev.librarymanagementsystem.entity.User;
import com.knf.dev.librarymanagementsystem.repository.UserRepository;
import com.knf.dev.librarymanagementsystem.service.BookService;

@SpringBootApplication
public class Application {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner initialCreate() {
        return (args) -> {

            // ✅ Only insert demo books if none exist
            if (bookService.getAllBooks().isEmpty()) {
                var book = new Book("AP1287", "Spring in Action ", "CXEF12389", "Book description");
                book.addAuthors(new Author("Matt", "dummy description"));
                book.addCategories(new Category("Dummy category"));
                book.addPublishers(new Publisher("Dummy publisher"));
                bookService.createBook(book);

                var book1 = new Book("BP567#R", "Spring Microservices", "KCXEF12389", "Description1");
                book1.addAuthors(new Author("Maxwell", "Test description1"));
                book1.addCategories(new Category("New category"));
                book1.addPublishers(new Publisher("publisher2"));
                bookService.createBook(book1);

                var book2 = new Book("GH67F#", "Spring Boot", "UV#JH", "description2");
                book2.addAuthors(new Author("Josh Lang", "Test description2"));
                book2.addCategories(new Category("Spring category"));
                book2.addPublishers(new Publisher("publisher3"));
                bookService.createBook(book2);
            }

            // ✅ Only insert Admin if missing
            if (userRepository.findByEmail("Admin@123") == null) {
                var admin = new User("Debjit", "Admin", "Admin@123", passwordEncoder.encode("password"),
                        Arrays.asList(new Role("ROLE_ADMIN")));
                userRepository.save(admin);
            }

            // ✅ Only insert Librarian if missing
            if (userRepository.findByEmail("Librarian@123") == null) {
                var librarian = new User("Arzu", "Librarian", "Librarian@123", passwordEncoder.encode("password"),
                        Arrays.asList(new Role("ROLE_LIBRARIAN")));
                userRepository.save(librarian);
            }

            // ✅ Only insert Member if missing
            if (userRepository.findByEmail("Member@123") == null) {
                var member = new User("Arun", "Member", "Member@123", passwordEncoder.encode("password"),
                        Arrays.asList(new Role("ROLE_MEMBER")));
                userRepository.save(member);
            }
        };
    }
}

