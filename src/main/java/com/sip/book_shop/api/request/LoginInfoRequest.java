package com.sip.book_shop.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginInfoRequest {

    @NotBlank(message = "{user.username.blank}")
    private String username;

    @NotBlank(message = "{user.password.blank}")
    private String password;
}
