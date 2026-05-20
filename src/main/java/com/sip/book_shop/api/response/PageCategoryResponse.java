package com.sip.book_shop.api.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.sip.book_shop.model.Category;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonPropertyOrder({"page", "totalPage", "totalDataCount", "categories"})
public class PageCategoryResponse {
    private int page;
    private int totalPage;
    private int totalDataCount;
    private List<Category> categories;
}
