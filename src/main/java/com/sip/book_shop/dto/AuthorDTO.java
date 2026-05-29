package com.sip.book_shop.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.sip.book_shop.constant.Constant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter @Setter
@JsonPropertyOrder({"id", "name", "birthDate"})
public class AuthorDTO {

    public Integer id;

    @NotBlank(message = "{author.name.blank}")
    public String name;

    @NotNull(message = "{author.birthDate.null}")
    @Past(message = "{author.birthDate.past}")
    @DateTimeFormat(pattern = Constant.DB_DATE_FORMAT)
    public LocalDate birthDate;

    @JsonProperty("birthDate")
    @JsonFormat
    public LocalDate getBirthDate() {
        return this.birthDate;
    }

    @JsonProperty("birthDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constant.DATE_FORMAT)
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
}