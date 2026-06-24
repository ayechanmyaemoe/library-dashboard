package com.sip.book_shop.web.dto;

import com.sip.book_shop.common.excel.query.ExcelColumn;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CategoryDTO {

    @ExcelColumn(index = 0, name = "ID")
    private Integer id;

    @ExcelColumn(index = 1, name = "Name")
    @NotBlank(message = "{category.name.blank}")
    private String name;
}
