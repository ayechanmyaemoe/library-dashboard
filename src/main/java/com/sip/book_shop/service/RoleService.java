package com.sip.book_shop.service;

import com.sip.book_shop.model.Role;
import com.sip.book_shop.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public Role getRoleUser() {
        return roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("User role not found!"));
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}
