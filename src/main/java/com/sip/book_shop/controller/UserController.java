package com.sip.book_shop.controller;

import com.sip.book_shop.config.UserDetailsImpl;
import com.sip.book_shop.dto.ChangePasswordDto;
import com.sip.book_shop.dto.UserDto;
import com.sip.book_shop.dto.UserUpdateDto;
import com.sip.book_shop.mapper.UserMapper;
import com.sip.book_shop.model.*;
import com.sip.book_shop.service.RoleService;
import com.sip.book_shop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("userDto", new UserDto());
        model.addAttribute("module", "register");
        return "register";
    }

    @PostMapping("/register")
    public String createAccount(@Valid @ModelAttribute UserDto userDto, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        model.addAttribute("module", "register");
        if(bindingResult.hasErrors()) {
            return "register";
        }

        if(userService.findByUsername(userDto.getUsername().trim()) != null) {
            bindingResult.rejectValue("username", "user.username.error.alreadyExists", "error.alreadyExists");
            return "register";
        }

        if(userService.findByEmail(userDto.getEmail().trim()) != null) {
            bindingResult.rejectValue("email", "user.email.error.alreadyExists", "error.alreadyExists");
            return "register";
        }

        if(!Objects.equals(userDto.getPassword().trim(), userDto.getConfirmPassword().trim())) {
            bindingResult.rejectValue("confirmPassword", "user.password.error.mismatch", "error.mismatch");
            return "register";
        }

        try {
            User user = userMapper.toEntity(userDto);
            user.setPassword(passwordEncoder.encode(userDto.getPassword().trim()));
            Role userRole = roleService.findByName("USER");
            user.setRole(userRole);
            userService.saveAccount(user);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/register?success";
    }

    @GetMapping
    public String showAllUsers(Model model) {
        model.addAttribute("module", "users");
        return "user-list-page";
    }

    @GetMapping("/add")
    public String addUser(Model model) {
        model.addAttribute("userDto", new UserDto());
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "add-user-page";
    }

    @PostMapping("/save")
    public String insertOrUpdateUser(@Valid @ModelAttribute UserDto userDto, BindingResult bindingResult, Model model) {
        model.addAttribute("allRoles", roleService.getAllRoles());
        if(bindingResult.hasErrors()) {
            return"add-user-page";
        }

        if(userService.findByUsername(userDto.getUsername().trim()) != null) {
            bindingResult.rejectValue("username", "user.username.error.alreadyExists", "error.alreadyExists");
            return "add-user-page";
        }

        if(userService.findByEmail(userDto.getEmail().trim()) != null) {
            bindingResult.rejectValue("email", "user.email.error.alreadyExists", "error.alreadyExists");
            return "add-user-page";
        }

        if(!Objects.equals(userDto.getPassword().trim(), userDto.getConfirmPassword().trim())) {
            bindingResult.rejectValue("confirmPassword", "user.password.error.mismatch", "error.mismatch");
            return "add-user-page";
        }
        User user = userMapper.toEntity(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword().trim()));
        user.setRole(userDto.getRole());
        userService.saveAccount(user);
        return "redirect:/users";
    }

    @GetMapping("/{id}/edit")
    public String editBook(Model model, @PathVariable int id) {
        User user = userService.getUserById(id);
        UserDto userDto = userMapper.toDto(user);
        model.addAttribute("userDto", userDto);
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "edit-user-page";
    }

    @PostMapping("/update")
    public String UpdateUser(@Valid @ModelAttribute("userDto") UserUpdateDto userUpdateDto, BindingResult bindingResult, Model model) {
        model.addAttribute("allRoles", roleService.getAllRoles());
        if(bindingResult.hasErrors()) {
            return "edit-user-page";
        }

        if(userUpdateDto.getId() != null) {
            User user = userService.getUserById(userUpdateDto.getId());
            if(!Objects.equals(user.getUsername(), userUpdateDto.getUsername())) {
                if(userService.findByUsername(userUpdateDto.getUsername().trim()) != null) {
                    bindingResult.rejectValue("username", "user.username.error.alreadyExists", "error.alreadyExists");
                    return "edit-user-page";
                }
            } else if(!Objects.equals(user.getEmail(), userUpdateDto.getEmail())) {
                if(userService.findByEmail(userUpdateDto.getEmail().trim()) != null) {
                    bindingResult.rejectValue("email", "user.email.error.alreadyExists", "error.alreadyExists");
                    return "edit-user-page";
                }
            }
            user.setUsername(userUpdateDto.getUsername().trim());
            user.setEmail(userUpdateDto.getEmail().trim());
            user.setRole(userUpdateDto.getRole());
            userService.saveAccount(user);
        }
        return "redirect:/users";
    }

    @DeleteMapping("/{id}")
    public String deleteBook(@AuthenticationPrincipal UserDetailsImpl currentUser, @PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUserById(currentUser.getUsername(), id);
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/users";
    }

    @GetMapping("/{id}/change-password")
    public String changePassword(@PathVariable int id, Model model) {
        User user = userService.getUserById(id);
        ChangePasswordDto changePasswordDto = userMapper.toChangePasswordDto(user);
        model.addAttribute("userDto", changePasswordDto);
        return "user-change-password-page";
    }

    @PostMapping("/save-password")
    public String SavePassword(@Valid @ModelAttribute("userDto") ChangePasswordDto changePasswordDto, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()) {
            return "user-change-password-page";
        }
        if(!Objects.equals(changePasswordDto.getPassword().trim(), changePasswordDto.getConfirmPassword().trim())) {
            bindingResult.rejectValue("confirmPassword", "user.password.error.mismatch", "error.mismatch");
            return "user-change-password-page";
        }

        if(changePasswordDto.getId() != null) {
            User user = userService.getUserById(changePasswordDto.getId());
            user.setPassword(passwordEncoder.encode(changePasswordDto.getPassword().trim()));
            userService.saveAccount(user);
            redirectAttributes.addFlashAttribute("successChangePassword", "You have successfully updated " + user.getUsername() +  "'s password!");
        }
        return "redirect:/users";
    }
}
