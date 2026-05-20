package com.sip.book_shop.api.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonPropertyOrder({"page", "totalPage", "totalDataCount", "users"})
public class PageUserResponse {

    private int page;
    private int totalPage;
    private int totalDataCount;
    private List<UserResponse> users;
}
