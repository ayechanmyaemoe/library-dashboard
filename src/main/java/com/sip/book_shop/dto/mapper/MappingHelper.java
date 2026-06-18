package com.sip.book_shop.dto.mapper;

import com.sip.book_shop.constant.Constant;
import com.sip.book_shop.entities.Author;
import com.sip.book_shop.entities.Category;
import com.sip.book_shop.entities.Role;
import com.sip.book_shop.repositories.AuthorRepository;
import com.sip.book_shop.repositories.CategoryRepository;
import com.sip.book_shop.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class MappingHelper {

    private final RoleRepository roleRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;

    @Named("trimString")
    public String trimString(String value) {
        return value != null ? value.trim() : null;
    }

    @Named("trimAndUpperCase")
    public String trimAndUpperCase(String value) {
        if (value == null) {
            return null;
        }
        return value.trim().toUpperCase();
    }

    @Named("roleUserToObj")
    public Role roleUserToObj(String roleStr) {
        return roleRepository.findByName(Constant.ROLE_USER)
                .orElseThrow(() -> new IllegalArgumentException("Role not found!"));
    }

    @Named("roleIdToObject")
    public Role roleIdToObject(int roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found!"));
    }

    @Named("validateBirthDate")
    public LocalDate validateBirthDate(LocalDate birthDate) {
        if(!birthDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Birthdate must be in the past!");
        }
        return birthDate;
    }

    @Named("authorIdToObject")
    public Author authorIdToObject(int authorId) {
        return authorRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("Author not found!"));
    }

    @Named("categoryIdToObject")
    public Category categoryIdToObject(int categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found!"));
    }
}
