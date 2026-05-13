package com.sip.book_shop.mapper;

import com.sip.book_shop.dto.AuthorDto;
import com.sip.book_shop.model.Author;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

    AuthorDto toDto(Author entity);

    Author toEntity(AuthorDto dto);

    default String trimString(String value) {
        return value != null ? value.trim() : null;
    }
}
