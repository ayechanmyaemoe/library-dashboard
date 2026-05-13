package com.sip.book_shop.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ChangePasswordDto {

    public Integer id;

    @NotBlank(message = "Password is required!")
    public String password;

    @NotBlank(message = "Confirm Password is required!")
    public String confirmPassword;
}
