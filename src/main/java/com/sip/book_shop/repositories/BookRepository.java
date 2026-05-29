package com.sip.book_shop.repositories;

import com.sip.book_shop.entities.Author;
import com.sip.book_shop.entities.Book;
import com.sip.book_shop.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer>, JpaSpecificationExecutor<Book> {

    List<Book> findByAuthorId(int authorId);

    List<Book> findByCategoryId(int categoryId);

    Optional<Book> findByTitleAndAuthorAndCategoryAndPublishedYear(
            String title, Author author, Category category, int publishedYear
    );
}
