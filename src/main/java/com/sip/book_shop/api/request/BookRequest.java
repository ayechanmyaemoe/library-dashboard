package com.sip.book_shop.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class BookRequest {

    @NotBlank(message = "{book.title.blank}")
    private String title;

    @NotBlank(message = "{book.publishedYear.blank}")
    @Pattern(regexp = "^\\d{4}$", message = "{book.publishedYear.pattern}")
    private String publishedYear;

    @NotBlank(message = "{book.author.blank}")
    private String author;

    @NotBlank(message = "{book.category.blank}")
    private String category;
}
