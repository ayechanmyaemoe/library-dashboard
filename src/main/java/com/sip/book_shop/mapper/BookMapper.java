package com.sip.book_shop.mapper;

import com.sip.book_shop.dto.BookDto;
import com.sip.book_shop.helper.MappingHelper;
import com.sip.book_shop.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = MappingHelper.class)
public interface BookMapper {

    BookDto toDto(Book entity);

    @Mapping(target = "title", source = "title", qualifiedByName = "trimString")
    Book toEntity(BookDto dto);
}
