package com.sip.book_shop.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ChangePasswordDto {

    public Integer id;

    @NotBlank(message = "{user.oldPassword.blank}")
    public String oldPassword;

    @NotBlank(message = "{user.newPassword.blank}")
    public String newPassword;

    @NotBlank(message = "{user.confirmPassword.blank}")
    public String confirmPassword;
}
