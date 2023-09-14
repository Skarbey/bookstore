package com.parkhomovsky.bookstore.repository.book;

import com.parkhomovsky.bookstore.model.Book;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository
        extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    @Query("SELECT b FROM Book b "
            + "LEFT JOIN FETCH b.categories c WHERE c.id = :categoryId")
    List<Book> findAllByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT b FROM Book b "
            + "LEFT JOIN FETCH b.categories c WHERE b.id = :bookId")
    Optional<Book> findByIdWithCategory(@Param("bookId") Long bookId);

    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.categories")
    List<Book> findAllWithCategory(Pageable pageable);
}
