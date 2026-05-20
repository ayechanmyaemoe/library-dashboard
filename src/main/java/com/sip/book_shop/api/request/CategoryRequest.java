package com.sip.book_shop.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequest {

    @NotBlank(message = "{category.name.blank}")
    private String name;
}
