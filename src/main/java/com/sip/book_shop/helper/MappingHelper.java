package com.sip.book_shop.helper;

import com.sip.book_shop.model.Author;
import com.sip.book_shop.model.Category;
import com.sip.book_shop.model.Role;
import com.sip.book_shop.repository.AuthorRepository;
import com.sip.book_shop.repository.CategoryRepository;
import com.sip.book_shop.repository.RoleRepository;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
public class MappingHelper {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private CategoryRepository categoryRepository;

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
        return roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalArgumentException("Role not found!"));
    }

    @Named("roleIdToObject")
    public Role roleIdToObject(int roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found!"));
    }

    @Named("ObjToRoleStr")
    public String ObjToRoleStr(Role role) {
        return role.getName();
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

    @Named("ObjToAuthorStr")
    public String ObjToAuthorStr(Author author) {
        return author.getName();
    }

    @Named("ObjToCategoryStr")
    public String ObjToCategoryStr(Category category) {
        return category.getName();
    }
}
