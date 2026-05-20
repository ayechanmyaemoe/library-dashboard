package com.sip.book_shop.mapper;

import com.sip.book_shop.api.request.CategoryRequest;
import com.sip.book_shop.dto.CategoryDto;
import com.sip.book_shop.helper.MappingHelper;
import com.sip.book_shop.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = MappingHelper.class)
public interface CategoryMapper {

    CategoryDto toDto(Category entity);

    @Mapping(target = "name", source = "name", qualifiedByName = "trimString")
    Category toEntity(CategoryDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "name", qualifiedByName = "trimString")
    Category toEntity(CategoryRequest request);
}
