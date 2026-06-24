package com.sip.book_shop.vo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddUserRequest {

    @NotBlank(message = "{user.username.blank}")
    private String username;

    @NotBlank(message = "{user.email.blank}")
    @Email(message = "{user.email.email}")
    private String email;

    @NotBlank(message = "{user.password.blank}")
    private String password;

    @NotNull(message = "{user.role.blank}")
    private int roleId;
}
