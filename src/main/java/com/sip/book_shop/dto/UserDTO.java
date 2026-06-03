package com.sip.book_shop.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.sip.book_shop.common.excel.query.ExcelColumn;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@JsonPropertyOrder({ "id", "username", "email", "enabled", "role" })
public class UserDTO {

    @ExcelColumn(index = 0, name = "ID")
    public Integer id;

    @ExcelColumn(index = 1, name = "Username")
    @NotBlank(message = "{user.username.blank}")
    public String username;

    @ExcelColumn(index = 2, name = "Email")
    @NotBlank(message = "{user.email.blank}")
    @Email(message = "{user.email.email}")
    public String email;

    @ExcelColumn(index = 3, name = "Enabled")
    private boolean enabled;

    @ExcelColumn(index = 4, name = "Role", nestedField = "name")
    public RoleDTO role;
}
