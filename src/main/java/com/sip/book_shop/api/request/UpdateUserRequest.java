package com.sip.book_shop.api.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUserRequest {
    @NotBlank(message = "{user.username.blank}")
    private String username;

    @NotBlank(message = "{user.email.blank}")
    @Email(message = "{user.email.email}")
    private String email;

    @NotBlank(message = "{user.role.blank}")
    private String role;
}
