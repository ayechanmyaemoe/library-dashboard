package com.sip.book_shop.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @NotBlank(message = "{user.oldPassword.blank}")
    private String oldPassword;

    @NotBlank(message = "{user.newPassword.blank}")
    private String newPassword;
}
