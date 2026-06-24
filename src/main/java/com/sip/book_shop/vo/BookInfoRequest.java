package com.sip.book_shop.vo;

import com.sip.book_shop.constant.Constant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class BookInfoRequest {

    @NotBlank(message = "{book.title.blank}")
    private String title;

    @NotBlank(message = "{book.publishedYear.blank}")
    @Pattern(regexp = Constant.YEAR_REGEX, message = "{book.publishedYear.pattern}")
    private String publishedYear;

    @NotNull(message = "{book.author.blank}")
    private int authorId;

    @NotNull(message = "{book.category.blank}")
    private int categoryId;
}
