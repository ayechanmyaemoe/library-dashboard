package com.sip.book_shop.dto;

import com.sip.book_shop.dto.validation.BlankCheck;
import com.sip.book_shop.dto.validation.PatternCheck;
import com.sip.book_shop.model.Author;
import com.sip.book_shop.model.Category;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@GroupSequence({BlankCheck.class, PatternCheck.class, BookDto.class})
public class BookDto {

    public Integer id;
    @NotBlank(message = "This cannot be blank!", groups = BlankCheck.class)
    public String title;

    @NotBlank(message = "This cannot be blank!", groups = BlankCheck.class)
    @Pattern(regexp = "^\\d{4}$", message = "published year must be a 4-digit number!", groups = PatternCheck.class)
    public String publishedYear;
    public Author author;
    public Category category;
}
