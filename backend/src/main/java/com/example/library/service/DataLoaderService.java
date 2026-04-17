package com.example.library.service;

import com.example.library.entity.Book;
import com.example.library.entity.TargetAudience;
import com.example.library.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsible for loading initial data into the database.
 * This service runs after Flyway migrations have completed.
 * 
 * Best practices implemented:
 * - Idempotent operations (safe to run multiple times)
 * - Proper logging for monitoring
 * - Transaction management
 * - Separation of concerns
 */
@Service
@Order(1000) // Run after other CommandLineRunners
public class DataLoaderService implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataLoaderService.class);

    private final BookRepository bookRepository;

    @Autowired
    public DataLoaderService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        logger.info("Starting data loading process...");
        
        try {
            loadInitialBooksIfNeeded();
            logger.info("Data loading completed successfully");
        } catch (Exception e) {
            logger.error("Error during data loading: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Load initial books if the database is empty.
     * This method is idempotent - it won't duplicate data if run multiple times.
     */
    private void loadInitialBooksIfNeeded() {
        long bookCount = bookRepository.count();
        logger.info("Current book count in database: {}", bookCount);

        if (bookCount == 0) {
            logger.info("Database is empty, loading initial books...");
            loadInitialBooks();
        } else {
            logger.info("Books already exist in database, skipping initial data load");
        }
    }

    /**
     * Load the initial set of books into the database.
     * Note: Flyway migration V2 should handle this, but this method serves as a backup
     * and can be used for additional data loading in development environments.
     */
    private void loadInitialBooks() {
        try {
            // Check if our specific books already exist
            if (!bookRepository.existsByTitleAndAuthor("Lo cercava Elia", "Autore Italiano")) {
                Book book1 = createBook(
                    "Lo cercava Elia",
                    "Ugo Grottoli",
                    "978-88-123-4567-8",
                    2023,
                    "Editore Italiano",
                    "Fiction",
                    TargetAudience.ADULT,
                    "IT",
                    "it",
                    320,
                    "Un romanzo avvincente che racconta la storia di Elia e la sua ricerca interiore attraverso paesaggi italiani mozzafiato."
                );
                bookRepository.save(book1);
                logger.info("Added book: {}", book1.getTitle());
            }

            if (!bookRepository.existsByTitleAndAuthor("Bla! bla!", "Scrittore Moderno")) {
                Book book2 = createBook(
                    "Bla! bla!",
                    "Scrittore Moderno",
                    "978-88-987-6543-2",
                    2024,
                    "Casa Editrice Contemporanea",
                    "Contemporary",
                    TargetAudience.YOUNG_ADULT,
                    "IT",
                    "it",
                    280,
                    "Una narrazione dinamica e coinvolgente che esplora temi contemporanei attraverso dialoghi brillanti e situazioni inaspettate."
                );
                bookRepository.save(book2);
                logger.info("Added book: {}", book2.getTitle());
            }

            logger.info("Initial books loaded successfully");
        } catch (Exception e) {
            logger.error("Failed to load initial books: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to load initial data", e);
        }
    }

    /**
     * Helper method to create a Book entity with all fields populated.
     */
    private Book createBook(String title, String author, String isbn, Integer publishedYear,
                           String publisher, String genre, TargetAudience targetAudience,
                           String country, String language, Integer pageCount, String description) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn(isbn);
        book.setPublishedYear(publishedYear);
        book.setPublisher(publisher);
        book.setGenre(genre);
        book.setTargetAudience(targetAudience);
        book.setCountry(country);
        book.setLanguage(language);
        book.setPageCount(pageCount);
        book.setDescription(description);
        return book;
    }
}