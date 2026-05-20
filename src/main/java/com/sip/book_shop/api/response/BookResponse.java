package com.sip.book_shop.api.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({"id", "title", "publishedYear", "author", "category"})
public class BookResponse {

    private int id;
    private String title;
    private String publishedYear;
    private String author;
    private String category;
}
