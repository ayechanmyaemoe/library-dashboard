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

    @NotBlank(message = "This cannot be blank!", groups = BlankCheck.class)
    public String name;

    @NotNull(message = "This cannot be blank!", groups = BlankCheck.class)
    @Past(message = "Birth date must be in the past!", groups = BirthDateValidCheck.class)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    public LocalDate birthDate;
}
