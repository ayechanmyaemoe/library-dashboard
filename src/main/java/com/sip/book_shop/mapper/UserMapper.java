package com.sip.book_shop.mapper;

import com.sip.book_shop.api.request.AddUserInfoRequest;
import com.sip.book_shop.api.request.ChangePasswordRequest;
import com.sip.book_shop.api.request.RegisterInfoRequest;
import com.sip.book_shop.api.request.UpdateUserRequest;
import com.sip.book_shop.api.response.UserResponse;
import com.sip.book_shop.dto.ChangePasswordDto;
import com.sip.book_shop.dto.UserDto;
import com.sip.book_shop.helper.MappingHelper;
import com.sip.book_shop.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {MappingHelper.class, RoleMapper.class})
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

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", source = "username", qualifiedByName = "trimString")
    @Mapping(target = "password", source = "password", qualifiedByName = "trimString")
    @Mapping(target = "email", source = "email", qualifiedByName = "trimString")
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "role", source = "role", qualifiedByName = "roleStrToObject")
    User toEntity(AddUserInfoRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", source = "username", qualifiedByName = "trimString")
    @Mapping(target = "password", source = "password", qualifiedByName = "trimString")
    @Mapping(target = "email", source = "email", qualifiedByName = "trimString")
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "role", qualifiedByName = "roleUserToObj")
    User toEntity(RegisterInfoRequest request);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "role", source = "role", qualifiedByName = "ObjToRoleStr")
    UserResponse toResponse(User entity);

    @Mapping(target = "username", source = "username", qualifiedByName = "trimString")
    @Mapping(target = "email", source = "email", qualifiedByName = "trimString")
    @Mapping(target = "role", source = "role", qualifiedByName = "roleStrToObject")
    User toEntity(UpdateUserRequest request);
}
