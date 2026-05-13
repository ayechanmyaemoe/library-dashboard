package com.sip.book_shop.mapper;

import com.sip.book_shop.dto.BookDto;
import com.sip.book_shop.model.Book;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookMapper {

    BookDto toDto(Book entity);

    Book toEntity(BookDto dto);

    default String trimString(String value) {
        return value != null ? value.trim() : null;
    }
}
