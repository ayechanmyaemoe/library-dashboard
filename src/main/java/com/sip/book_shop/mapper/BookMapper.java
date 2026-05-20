package com.sip.book_shop.mapper;

import com.sip.book_shop.api.request.BookRequest;
import com.sip.book_shop.api.response.BookResponse;
import com.sip.book_shop.dto.BookDto;
import com.sip.book_shop.helper.MappingHelper;
import com.sip.book_shop.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {MappingHelper.class, AuthorMapper.class, CategoryMapper.class})
public interface BookMapper {

    BookDto toDto(Book entity);

    @Mapping(target = "title", source = "title", qualifiedByName = "trimString")
    @Mapping(target = "publishedYear", source = "publishedYear")
    Book toEntity(BookDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "title", source = "title", qualifiedByName = "trimString")
    @Mapping(target = "author", source = "author", qualifiedByName = "authorStrToObject")
    @Mapping(target = "category", source = "category", qualifiedByName = "categoryStrToObject")
    Book toEntity(BookRequest request);

    @Mapping(target = "author", source = "author", qualifiedByName = "ObjToAuthorStr")
    @Mapping(target = "category", source = "category", qualifiedByName = "ObjToCategoryStr")
    BookResponse toResponse(Book entity);
}
