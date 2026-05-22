package com.sip.book_shop.api.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.sip.book_shop.model.Category;
import lombok.Data;

@Data
@JsonPropertyOrder({"id", "title", "publishedYear", "author", "category"})
public class SingleBookResponse {

    private int id;
    private String title;
    private String publishedYear;
    private AuthorResponse author;
    private Category category;
}
