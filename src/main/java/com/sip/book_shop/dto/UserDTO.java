package com.sip.book_shop.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.sip.book_shop.common.excel.query.ExcelColumn;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@JsonPropertyOrder({ "id", "username", "email", "enabled", "role" })
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {

    @ExcelColumn(index = 0, name = "ID")
    private Integer id;

    @ExcelColumn(index = 1, name = "Username")
    @NotBlank(message = "{user.username.blank}")
    private String username;

    @ExcelColumn(index = 2, name = "Email")
    @NotBlank(message = "{user.email.blank}")
    @Email(message = "{user.email.email}")
    private String email;

    @ExcelColumn(index = 3, name = "Enabled")
    private boolean enabled;

    @ExcelColumn(index = 4, name = "Role", nestedField = "name")
    private RoleDTO role;
}

//
