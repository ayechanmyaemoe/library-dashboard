package com.sip.book_shop.repository;

import com.sip.book_shop.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    Role findByName(String name);

    @Query("select r from Role r where " +
            "lower(r.name) like lower(concat('%', :searchValue, '%'))")
    Page<Role> searchByKeyword(@Param("searchValue") String searchValue, Pageable pageable);
}
