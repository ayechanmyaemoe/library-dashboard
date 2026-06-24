package com.sip.book_shop.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "{user.username.blank}")
    private String username;

    @NotBlank(message = "{user.password.blank}")
    private String password;
}
