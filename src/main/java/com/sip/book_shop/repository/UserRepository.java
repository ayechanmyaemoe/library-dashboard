package com.sip.book_shop.repository;

import com.sip.book_shop.model.Book;
import com.sip.book_shop.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findByUsername(String username);

    User findByEmail(String email);

    List<User> findByRoleId(int roleId);

    @Query("select u from User u where " +
            "lower(u.username) like lower(concat('%', :searchValue, '%')) or " +
            "lower(u.email) like concat('%', :searchValue, '%')")
    Page<User> searchByKeyword(@Param("searchValue") String searchValue, Pageable pageable);
}
