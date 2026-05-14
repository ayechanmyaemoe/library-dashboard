package com.sip.book_shop.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CategoryDto {

    private Integer id;

    @NotBlank(message = "{category.name.blank}")
    private String name;
}
