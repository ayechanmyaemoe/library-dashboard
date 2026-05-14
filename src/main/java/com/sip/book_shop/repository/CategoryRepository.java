package com.sip.book_shop.repository;

import com.sip.book_shop.model.Author;
import com.sip.book_shop.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Category findByName(String name);

    @Query("select c from Category c where " +
            "lower(c.name) like lower(concat('%', :searchValue, '%'))")
    Page<Category> searchByKeyword(@Param("searchValue") String searchValue, Pageable pageable);
}
