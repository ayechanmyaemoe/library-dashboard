package com.sip.book_shop.mapper;

import com.sip.book_shop.dto.AuthorDto;
import com.sip.book_shop.helper.MappingHelper;
import com.sip.book_shop.model.Author;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = MappingHelper.class)
public interface AuthorMapper {

    AuthorDto toDto(Author entity);

    @Mapping(target = "name", source = "name", qualifiedByName = "trimString")
    Author toEntity(AuthorDto dto);
}
