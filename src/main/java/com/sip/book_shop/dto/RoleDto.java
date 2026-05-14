package com.sip.book_shop.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RoleDto {

    private Integer id;

    @NotNull(message = "{role.name.blank}")
    private String name;
}
