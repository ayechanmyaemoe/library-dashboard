package com.sip.book_shop.repository;

import com.sip.book_shop.model.Author;
import com.sip.book_shop.model.Book;
import com.sip.book_shop.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {

    List<Book> findByAuthorId(int authorId);

    List<Book> findByCategoryId(int categoryId);

    @Query("select b from Book b where " +
            "lower(b.title) like lower(concat('%', :searchValue, '%')) or " +
            "lower(b.author.name) like lower(concat('%', :searchValue, '%')) or " +
            "lower(b.category.name) like lower(concat('%', :searchValue, '%'))")
    Page<Book> searchByKeyword(@Param("searchValue") String searchValue, Pageable pageable);

    Optional<Book> findByTitleAndAuthorAndCategoryAndPublishedYear(
            String title, Author author, Category category, int publishedYear
    );
}
