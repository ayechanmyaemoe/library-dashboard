package com.sip.book_shop.dto;

import com.sip.book_shop.dto.validation.BlankCheck;
import com.sip.book_shop.model.Role;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@GroupSequence({BlankCheck.class, UserDto.class})
public class UserDto {

    public Integer id;

    @NotBlank(message = "Username is required!", groups = BlankCheck.class)
    public String username;

    @NotBlank(message = "Email is required!", groups = BlankCheck.class)
    @Email(message = "Invalid email address!")
    public String email;

    @NotBlank(message = "Password is required!", groups = BlankCheck.class)
    public String password;

    @NotBlank(message = "Confirm Password is required!", groups = BlankCheck.class)
    public String confirmPassword;

    public Role role;
}
