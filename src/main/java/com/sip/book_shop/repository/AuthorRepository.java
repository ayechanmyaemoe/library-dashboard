package com.sip.book_shop.repository;

import com.sip.book_shop.model.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Integer> {

    @Query("select a from Author a where " +
            "lower(a.name) like lower(concat('%', :searchValue, '%')) or " +
            "cast(a.birthDate as string) like concat('%', :searchValue, '%')")
    Page<Author> searchByKeyword(@Param("searchValue") String searchValue, Pageable pageable);
}
