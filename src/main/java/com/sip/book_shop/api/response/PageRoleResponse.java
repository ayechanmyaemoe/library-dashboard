package com.sip.book_shop.api.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.sip.book_shop.model.Role;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonPropertyOrder({"page", "totalPage", "totalDataCount", "roles"})
public class PageRoleResponse {
    private int page;
    private int totalPage;
    private int totalDataCount;
    private List<Role> roles;
}
