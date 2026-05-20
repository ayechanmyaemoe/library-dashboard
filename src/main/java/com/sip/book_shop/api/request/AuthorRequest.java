package com.sip.book_shop.api.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AuthorRequest {

    @NotBlank(message = "{author.name.blank}")
    private String name;

    @NotNull(message = "{author.birthDate.null}")
    @Past(message = "{author.birthDate.past}")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate birthDate;
}
