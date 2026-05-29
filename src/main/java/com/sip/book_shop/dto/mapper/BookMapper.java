package com.sip.book_shop.dto.mapper;

import com.sip.book_shop.dto.BookDTO;
import com.sip.book_shop.vo.BookInfoRequest;
import com.sip.book_shop.entities.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {MappingHelper.class, AuthorMapper.class, CategoryMapper.class})
public interface BookMapper {

    BookDTO toDto(Book entity);

    @Mapping(target = "title", source = "title", qualifiedByName = "trimString")
    @Mapping(target = "publishedYear", source = "publishedYear")
    Book toEntity(BookDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "title", source = "title", qualifiedByName = "trimString")
    @Mapping(target = "author", source = "authorId", qualifiedByName = "authorIdToObject")
    @Mapping(target = "category", source = "categoryId", qualifiedByName = "categoryIdToObject")
    Book toEntity(BookInfoRequest request);
}
