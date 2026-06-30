package com.sip.book_shop.web.dto;

import com.sip.book_shop.common.excel.query.ExcelColumn;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RoleDTO {

    @ExcelColumn(index = 0, name = "ID")
    private Integer id;

    @ExcelColumn(index = 1, name = "Name")
    @NotNull(message = "{role.name.blank}")
    private String name;
}
