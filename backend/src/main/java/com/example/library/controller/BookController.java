package com.example.library.controller;

import com.example.library.entity.Book;
import com.example.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "http://localhost:5173") // For React dev server
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/{id}")
    public Optional<Book> getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @PostMapping
    public Book createBook(@RequestBody Book book) {
        return bookService.saveBook(book);
    }

    @PutMapping("/{id}")
    public Book updateBook(@PathVariable Long id, @RequestBody Book book) {
        book.setId(id);
        return bookService.saveBook(book);
    }

    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }

    /**
     * Search books by keyword across title, author, and ISBN
     * GET /api/books/search?query=keyword
     */
    @GetMapping("/search")
    public List<Book> searchBooks(@RequestParam(value = "query", required = false) String query) {
        return bookService.searchBooks(query);
    }

    /**
     * Search books by title
     * GET /api/books/search/title?title=keyword
     */
    @GetMapping("/search/title")
    public List<Book> searchByTitle(@RequestParam String title) {
        return bookService.searchByTitle(title);
    }

    /**
     * Search books by author
     * GET /api/books/search/author?author=keyword
     */
    @GetMapping("/search/author")
    public List<Book> searchByAuthor(@RequestParam String author) {
        return bookService.searchByAuthor(author);
    }

    /**
     * Search books by ISBN
     * GET /api/books/search/isbn?isbn=value
     */
    @GetMapping("/search/isbn")
    public List<Book> searchByIsbn(@RequestParam String isbn) {
        return bookService.searchByIsbn(isbn);
    }

    /**
     * Search books by published year
     * GET /api/books/search/year?year=value
     */
    @GetMapping("/search/year")
    public List<Book> searchByYear(@RequestParam Integer year) {
        return bookService.searchByPublishedYear(year);
    }
}
