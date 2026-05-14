package com.sip.book_shop.controller;

import com.sip.book_shop.dto.RoleDto;
import com.sip.book_shop.mapper.RoleMapper;
import com.sip.book_shop.model.Role;
import com.sip.book_shop.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

@Controller
@RequestMapping("/roles")
public class UserRoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleMapper roleMapper;

    public UserRoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public String getAllRoles(Model model) {
        model.addAttribute("module", "roles");
        return "role-list-page";
    }

    @GetMapping("/add")
    public String addRole(Model model) {
        model.addAttribute("roleDto", new RoleDto());
        return "add-role-page";
    }

    @PostMapping("/save")
    public String insertOrUpdateRole(@Valid @ModelAttribute RoleDto roleDto, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            if(roleDto.getId() != null) {
                return "edit-role-page";
            } else {
                return "add-role-page";
            }
        }

        Role role;
        if(roleDto.getId() != null) {
            role = roleService.getRoleById(roleDto.getId());
            if(!Objects.equals(role.getName(), roleDto.getName())) {
                if(roleService.findByName(roleDto.getName().trim()) != null) {
                    bindingResult.rejectValue("name", "role.error.alreadyExists", "error.alreadyExists");
                    return "edit-role-page";
                }
            }
            role.setName(roleDto.getName().trim().toUpperCase());
        } else {
            if(roleService.findByName(roleDto.getName().trim()) != null) {
                bindingResult.rejectValue("name", "role.error.alreadyExists", "error.alreadyExists");
                return "add-role-page";
            }
            role = roleMapper.toEntity(roleDto);
        }
        roleService.saveRole(role);
        return "redirect:/roles";
    }

    @GetMapping("/{id}/edit")
    public String editRole(Model model, @PathVariable int id) {
        Role role = roleService.getRoleById(id);
        RoleDto roleDto = roleMapper.toDto(role);
        model.addAttribute("roleDto", roleDto);
        return "edit-role-page";
    }

    @DeleteMapping("/{id}")
    public String deleteRole(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            roleService.deleteRoleById(id);
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/roles";
    }
}
