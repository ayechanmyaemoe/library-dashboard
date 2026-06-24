package com.sip.book_shop.web.dto.mapper;

import com.sip.book_shop.web.dto.CategoryDTO;
import com.sip.book_shop.web.dto.mapper.helper.MappingHelper;
import com.sip.book_shop.entities.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = MappingHelper.class)
public interface CategoryMapper {

    CategoryDTO toDto(Category entity);

    @Mapping(target = "name", source = "name", qualifiedByName = "trimString")
    Category toEntity(CategoryDTO dto);
}
