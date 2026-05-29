package com.sip.book_shop.dto.mapper;

import com.sip.book_shop.dto.AuthorDTO;
import com.sip.book_shop.entities.Author;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = MappingHelper.class)
public interface AuthorMapper {

    AuthorDTO toDto(Author entity);

    @Mapping(target = "name", source = "name", qualifiedByName = "trimString")
    Author toEntity(AuthorDTO dto);
}
