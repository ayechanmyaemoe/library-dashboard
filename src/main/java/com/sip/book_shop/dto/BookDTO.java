package com.sip.book_shop.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.sip.book_shop.constant.Constant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@JsonPropertyOrder({"id", "title", "publishedYear", "author", "category"})
public class BookDTO {

    public Integer id;

    @NotBlank(message = "{book.title.blank}")
    public String title;

    @NotBlank(message = "{book.publishedYear.blank}")
    @Pattern(regexp = Constant.YEAR_REGEX, message = "{book.publishedYear.pattern}")
    public String publishedYear;

    public AuthorDTO author;

    public CategoryDTO category;
}
