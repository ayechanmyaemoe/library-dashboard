package com.sip.book_shop.api.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({ "id", "username", "email", "enabled", "role" })
public class UserResponse {
    private int id;
    private String username;
    private String email;
    private boolean enabled;
    private String role;
}
