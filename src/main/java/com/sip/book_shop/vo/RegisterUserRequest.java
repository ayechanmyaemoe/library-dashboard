package com.sip.book_shop.vo;

import com.sip.book_shop.entities.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterUserRequest {

    @NotBlank(message = "{user.username.blank}")
    private String username;

    @NotBlank(message = "{user.email.blank}")
    @Email(message = "{user.email.email}")
    private String email;

    @NotBlank(message = "{user.password.blank}")
    private String password;

    private String role;
}
