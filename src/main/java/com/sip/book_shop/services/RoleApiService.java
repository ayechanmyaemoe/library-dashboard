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
import org.springframework.data.jpa.domain.Specification;
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

    public List<RoleDTO> getAllResult(RoleQueryCriteria criteria) {
        Specification<Role> specification = (root, cq, cb) -> QueryHelper.getPredicate(root, criteria, cq, cb);
        Page<Role> pageBooks = roleRepository.findAll(specification, criteria.getPageable());

        List<RoleDTO> responseRoles = new ArrayList<>();
        for(Role role: pageBooks) {
            responseRoles.add(roleMapper.toDto(role));
        }

        return responseRoles;
    }

    public long count() {
        return roleRepository.count();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public RoleDTO findById(int id) {
        return roleMapper.toDto(getExistingRole(id));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void addNew(RoleDTO request) {
        Role role = roleMapper.toEntity(request);
        checkRoleExists(role.getName());
        roleRepository.save(role);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void update(RoleDTO request, int id) {
        Role existingRole = getExistingRole(id);
        if(!Objects.equals(existingRole.getName(), request.getName().toUpperCase().trim())) {
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
