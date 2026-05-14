package com.sip.book_shop.mapper;

import com.sip.book_shop.dto.ChangePasswordDto;
import com.sip.book_shop.dto.UserDto;
import com.sip.book_shop.helper.MappingHelper;
import com.sip.book_shop.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = MappingHelper.class)
public interface UserMapper {

    UserDto toDto(User entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "username", source = "username", qualifiedByName = "trimString")
    @Mapping(target = "email", source = "email", qualifiedByName = "trimString")
    User toEntity(UserDto dto);

    ChangePasswordDto toChangePasswordDto(User entity);
}
