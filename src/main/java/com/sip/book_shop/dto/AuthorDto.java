package com.sip.book_shop.dto;

import com.sip.book_shop.dto.validation.BirthDateValidCheck;
import com.sip.book_shop.dto.validation.BlankCheck;
import com.sip.book_shop.dto.validation.PatternCheck;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter @Setter
@GroupSequence({BlankCheck.class, PatternCheck.class, BirthDateValidCheck.class, AuthorDto.class})
public class AuthorDto {

    public Integer id;

    @NotBlank(message = "{author.name.blank}", groups = BlankCheck.class)
    public String name;

    @NotNull(message = "{author.birthDate.null}", groups = BlankCheck.class)
    @Past(message = "{author.birthDate.past}", groups = BirthDateValidCheck.class)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    public LocalDate birthDate;
}
