package com.sip.book_shop.controller;

import com.sip.book_shop.dto.RoleDto;
import com.sip.book_shop.exception.AlreadyExistsException;
import com.sip.book_shop.helper.MessageHelper;
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
    public String insertOrUpdateRole(@Valid @ModelAttribute RoleDto roleDto,
                                     BindingResult bindingResult,
                                     RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()) {
            return (roleDto.getId() != null) ? "edit-role-page" : "add-role-page";
        }

        boolean isUpdate = roleDto.getId() != null;
        try {
            Role role;
            if(isUpdate) {
                role = roleService.getRoleById(roleDto.getId());
                role.setName(roleDto.getName());
            } else {
                role = roleMapper.toEntity(roleDto);
            }
            roleService.saveRole(role);
            String messageKey = isUpdate ? "role.success.edit" : "role.success.create";
            redirectAttributes.addFlashAttribute("success", MessageHelper.getMessage(messageKey));
        } catch (AlreadyExistsException e) {
            bindingResult.rejectValue("name", e.getMessage(), "error.alreadyExists");
            return isUpdate ? "edit-role-page" : "add-role-page";
        }
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
