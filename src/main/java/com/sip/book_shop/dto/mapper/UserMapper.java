package com.sip.book_shop.dto.mapper;

import com.sip.book_shop.dto.UserDTO;
import com.sip.book_shop.vo.AddUserRequest;
import com.sip.book_shop.vo.RegisterUserRequest;
import com.sip.book_shop.vo.UpdateUserRequest;
import com.sip.book_shop.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {MappingHelper.class, RoleMapper.class})
public interface UserMapper {

    UserDTO toDto(User entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "username", source = "username", qualifiedByName = "trimString")
    @Mapping(target = "email", source = "email", qualifiedByName = "trimString")
    User toEntity(UserDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", source = "username", qualifiedByName = "trimString")
    @Mapping(target = "password", source = "password", qualifiedByName = "trimString")
    @Mapping(target = "email", source = "email", qualifiedByName = "trimString")
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "role", source = "roleId", qualifiedByName = "roleIdToObject")
    User toEntity(AddUserRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", source = "username", qualifiedByName = "trimString")
    @Mapping(target = "password", source = "password", qualifiedByName = "trimString")
    @Mapping(target = "email", source = "email", qualifiedByName = "trimString")
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "role", qualifiedByName = "roleUserToObj")
    User toEntity(RegisterUserRequest request);

    @Mapping(target = "username", source = "username", qualifiedByName = "trimString")
    @Mapping(target = "email", source = "email", qualifiedByName = "trimString")
    @Mapping(target = "role", source = "roleId", qualifiedByName = "roleIdToObject")
    User toEntity(UpdateUserRequest request);
}
