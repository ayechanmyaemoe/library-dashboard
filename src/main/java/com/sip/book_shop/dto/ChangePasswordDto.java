package com.sip.book_shop.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ChangePasswordDto {

    public Integer id;

    @NotBlank(message = "{user.password.blank}")
    public String password;

    @NotBlank(message = "{user.confirmPassword.blank}")
    public String confirmPassword;
}
