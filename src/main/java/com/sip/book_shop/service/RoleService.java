package com.sip.book_shop.service;

import com.sip.book_shop.exception.AlreadyExistsException;
import com.sip.book_shop.helper.MessageHelper;
import com.sip.book_shop.model.Role;
import com.sip.book_shop.model.User;
import com.sip.book_shop.repository.RoleRepository;
import com.sip.book_shop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Page<Role> searchRoles(String searchValue, Pageable pageable) {
        return roleRepository.searchByKeyword(searchValue, pageable);
    }

    public Page<Role> findPaginated(Pageable pageable) {
        return roleRepository.findAll(pageable);
    }

    public int countAllRoles() {
        return roleRepository.findAll().size();
    }

    public Role findByName(String name) {
        return roleRepository.findByName(name).orElse(null);
    }

    public Role getRoleById(int id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(MessageHelper.getMessage("role.error.notFound")));
    }

    public void saveRole(Role role) {
        String modifiedName = role.getName().trim().toUpperCase();
        Optional<Role> existingRole = roleRepository.findByName(modifiedName);
        if (existingRole.isPresent() && (role.getId() == 0 || existingRole.get().getId() != role.getId())) {
            throw new AlreadyExistsException("role.error.alreadyExists");
        }
        role.setName(modifiedName);
        roleRepository.save(role);
    }

    public void deleteRoleById(int id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(MessageHelper.getMessage("role.error.notFound")));

        List<User> users = userRepository.findByRoleId(role.getId());

        if(!users.isEmpty()) {
            throw new IllegalStateException(MessageHelper.getMessage("role.error.denyDeletion"));
        }

        roleRepository.deleteById(role.getId());
    }
}
