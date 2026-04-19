package com.example.library.repository;

import com.example.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByTitleIgnoreCaseContaining(String title);

    List<Book> findByAuthorIgnoreCaseContaining(String author);

    List<Book> findByIsbn(String isbn);

    List<Book> findByPublishedYear(Integer publishedYear);

    /**
     * Combined search across multiple fields
     */
    @Query("SELECT b FROM Book b WHERE " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "b.isbn LIKE CONCAT('%', :keyword, '%')")
    List<Book> searchByKeyword(@Param("keyword") String keyword);

    /**
     * Check if a book exists by title and author (for data loading)
     */
    boolean existsByTitleAndAuthor(String title, String author);
}
