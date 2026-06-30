package com.sip.book_shop.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequest {

    @NotBlank(message = "{user.refresh_token.blank}")
    private String refreshToken;
}
