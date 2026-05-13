package com.sip.book_shop.dto;

import com.sip.book_shop.dto.validation.BlankCheck;
import com.sip.book_shop.model.Role;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@GroupSequence({BlankCheck.class, UserUpdateDto.class})
public class UserUpdateDto {

    private Integer id;

    @NotBlank(message = "Username is required!", groups = BlankCheck.class)
    private String username;

    @NotBlank(message = "Email is required!", groups = BlankCheck.class)
    @Email(message = "Invalid email address!")
    private String email;

    private Role role;
}
