package com.sip.book_shop.mapper;

import com.sip.book_shop.api.request.RoleRequest;
import com.sip.book_shop.dto.RoleDto;
import com.sip.book_shop.helper.MappingHelper;
import com.sip.book_shop.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = MappingHelper.class)
public interface RoleMapper {

    RoleDto toDto(Role entity);

    @Mapping(target = "name", source = "name", qualifiedByName = "trimAndUpperCase")
    Role toEntity(RoleDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "name", qualifiedByName = "trimAndUpperCase")
    Role toEntity(RoleRequest request);
}
