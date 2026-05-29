package com.sip.book_shop.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@JsonPropertyOrder({ "id", "username", "email", "enabled", "role" })
public class UserDTO {

    public Integer id;

    @NotBlank(message = "{user.username.blank}")
    public String username;

    @NotBlank(message = "{user.email.blank}")
    @Email(message = "{user.email.email}")
    public String email;

    private boolean enabled;

    public RoleDTO role;
}
