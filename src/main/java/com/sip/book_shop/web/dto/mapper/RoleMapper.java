package com.sip.book_shop.web.dto.mapper;

import com.sip.book_shop.web.dto.RoleDTO;
import com.sip.book_shop.web.dto.mapper.helper.MappingHelper;
import com.sip.book_shop.entities.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = MappingHelper.class)
public interface RoleMapper {

    RoleDTO toDto(Role entity);

    @Mapping(target = "name", source = "name", qualifiedByName = "trimAndUpperCase")
    Role toEntity(RoleDTO dto);
}
