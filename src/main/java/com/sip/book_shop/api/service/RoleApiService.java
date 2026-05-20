package com.sip.book_shop.api.service;

import com.sip.book_shop.api.request.RoleRequest;
import com.sip.book_shop.api.response.PageRoleResponse;
import com.sip.book_shop.api.response.UserResponse;
import com.sip.book_shop.exception.AlreadyExistsException;
import com.sip.book_shop.exception.NotAllowedException;
import com.sip.book_shop.exception.NotFoundException;
import com.sip.book_shop.mapper.RoleMapper;
import com.sip.book_shop.model.Role;
import com.sip.book_shop.model.User;
import com.sip.book_shop.repository.RoleRepository;
import com.sip.book_shop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class RoleApiService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleMapper roleMapper;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public PageRoleResponse getAll(int page, int size, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by("id").ascending() : Sort.by("id").descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        List<Role> responseRoles = new ArrayList<>();
        Page<Role> roles = roleRepository.findAll(pageable);
        for(Role role: roles) {
            responseRoles.add(role);
        }

        List<Role> allRoles = roleRepository.findAll();
        return PageRoleResponse.builder()
                .page(page)
                .totalPage(roles.getTotalPages())
                .totalDataCount(allRoles.size())
                .roles(responseRoles)
                .build();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void addNew(RoleRequest request) {
        Role role = roleMapper.toEntity(request);
        checkRoleExists(role.getName());
        roleRepository.save(role);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void update(RoleRequest request, int id) {
        Role existingRole = getExistingRole(id);
        if(!Objects.equals(existingRole.getName(), request.getName().trim())) {
            checkRoleExists(request.getName().trim());
        }
        Role role = roleMapper.toEntity(request);
        role.setId(id);
        roleRepository.save(role);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void delete(int id) {
        Role existingRole = getExistingRole(id);
        List<User> existingUsers = userRepository.findByRoleId(id);
        if(!existingUsers.isEmpty()) {
            throw new NotAllowedException("There are still users with this role. Please delete them first!");
        }
        roleRepository.deleteById(existingRole.getId());
    }

    private void checkRoleExists(String name) {
        boolean isExists = roleRepository.existsByName(name);
        if(isExists) {
            throw new AlreadyExistsException("Role already existed!");
        }
    }

    private Role getExistingRole(int id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("There is no role with such id."));
    }
}
