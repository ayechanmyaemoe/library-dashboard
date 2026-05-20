package com.sip.book_shop.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleRequest {

    @NotBlank(message = "{user.role.blank}")
    private String name;
}
