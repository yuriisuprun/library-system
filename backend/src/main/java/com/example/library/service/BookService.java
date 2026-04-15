package com.example.library.service;

import com.example.library.entity.Book;
import com.example.library.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    /**
     * Search books by keyword across title, author, and ISBN
     */
    public List<Book> searchBooks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllBooks();
        }
        return bookRepository.searchByKeyword(keyword.trim());
    }

    /**
     * Search books by title
     */
    public List<Book> searchByTitle(String title) {
        return bookRepository.findByTitleIgnoreCaseContaining(title);
    }

    /**
     * Search books by author
     */
    public List<Book> searchByAuthor(String author) {
        return bookRepository.findByAuthorIgnoreCaseContaining(author);
    }

    /**
     * Search books by ISBN
     */
    public List<Book> searchByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    /**
     * Search books by published year
     */
    public List<Book> searchByPublishedYear(Integer year) {
        return bookRepository.findByPublishedYear(year);
    }
}
