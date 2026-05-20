package com.sip.book_shop.api.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.sip.book_shop.model.Author;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonPropertyOrder({"page", "totalPage", "totalDataCount", "authors"})
public class PageAuthorResponse {
    private int page;
    private int totalPage;
    private int totalDataCount;
    private List<Author> authors;
}
