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

    @Named("roleStrToObject")
    public Role roleStrToObject(String roleStr) {
        return roleRepository.findByName(roleStr)
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

    @Named("authorStrToObject")
    public Author authorStrToObject(String authorStr) {
        return authorRepository.findByName(authorStr)
                .orElseThrow(() -> new IllegalArgumentException("Author not found!"));
    }

    @Named("categoryStrToObject")
    public Category categoryStrToObject(String categoryStr) {
        return categoryRepository.findByName(categoryStr)
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
