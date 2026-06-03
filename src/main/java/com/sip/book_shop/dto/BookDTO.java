package com.sip.book_shop.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.sip.book_shop.common.excel.query.ExcelColumn;
import com.sip.book_shop.constant.Constant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@JsonPropertyOrder({"id", "title", "publishedYear", "author", "category"})
public class BookDTO {

    @ExcelColumn(index = 0, name = "ID")
    public Integer id;

    @ExcelColumn(index = 1, name = "Title")
    @NotBlank(message = "{book.title.blank}")
    public String title;

    @ExcelColumn(index = 2, name = "Published Year")
    @NotBlank(message = "{book.publishedYear.blank}")
    @Pattern(regexp = Constant.YEAR_REGEX, message = "{book.publishedYear.pattern}")
    public String publishedYear;

    @ExcelColumn(index = 3, name = "Author", nestedField = "name")
    public AuthorDTO author;

    @ExcelColumn(index = 4, name = "Category", nestedField = "name")
    public CategoryDTO category;
}
