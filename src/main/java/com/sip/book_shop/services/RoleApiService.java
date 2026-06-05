package com.sip.book_shop.services;

import com.sip.book_shop.common.query.QueryHelper;
import com.sip.book_shop.dto.RoleDTO;
import com.sip.book_shop.dto.mapper.RoleMapper;
import com.sip.book_shop.entities.queryCriteria.RoleQueryCriteria;
import com.sip.book_shop.common.exceptions.AlreadyExistsException;
import com.sip.book_shop.common.exceptions.NotAllowedException;
import com.sip.book_shop.common.exceptions.NotFoundException;
import com.sip.book_shop.entities.Role;
import com.sip.book_shop.entities.User;
import com.sip.book_shop.repositories.RoleRepository;
import com.sip.book_shop.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;

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
    public Page<RoleDTO> getAllResult(RoleQueryCriteria criteria) {
        Specification<Role> specification = (root, cq, cb) -> QueryHelper.getPredicate(root, criteria, cq, cb);
        Page<Role> pageBooks = roleRepository.findAll(specification, criteria.getPageable());

        List<RoleDTO> responseRoles = pageBooks.stream().map(roleMapper::toDto)
                .toList();

        return new PageImpl<>(responseRoles, pageBooks.getPageable(), pageBooks.getTotalElements());
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<RoleDTO> getAll() {
        List<RoleDTO> roleDTOs = new ArrayList<>();
        List<Role> roles = roleRepository.findAll();
        for(Role role: roles) {
            roleDTOs.add(roleMapper.toDto(role));
        }
        return roleDTOs;
    }

    public long count() {
        return roleRepository.count();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public RoleDTO findById(int id) {
        return roleMapper.toDto(getExistingRole(id));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void addNew(RoleDTO request) throws BindException {
        Role role = roleMapper.toEntity(request);
        checkRoleExists(request);
        roleRepository.save(role);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void update(RoleDTO request, int id) throws BindException {
        Role existingRole = getExistingRole(id);
        if(!Objects.equals(existingRole.getName(), request.getName().toUpperCase().trim())) {
            checkRoleExists(request);
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

    private void checkRoleExists(RoleDTO request) throws BindException {
        boolean isExists = roleRepository.existsByName(request.getName().trim());
        if(isExists) {
            BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(request, "roleDTO");
            bindingResult.rejectValue("name", "duplicate", "Role already existed!");
            throw new BindException(bindingResult);
        }
    }

    private Role getExistingRole(int id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("There is no role with such id."));
    }
}
