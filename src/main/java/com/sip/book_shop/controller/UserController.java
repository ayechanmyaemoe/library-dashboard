package com.sip.book_shop.controller;

import com.sip.book_shop.config.UserDetailsImpl;
import com.sip.book_shop.dto.ChangePasswordDto;
import com.sip.book_shop.dto.UserDto;
import com.sip.book_shop.dto.UserUpdateDto;
import com.sip.book_shop.exception.AlreadyExistsException;
import com.sip.book_shop.exception.MisMatchException;
import com.sip.book_shop.helper.MessageHelper;
import com.sip.book_shop.mapper.RoleMapper;
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

    @Autowired
    private RoleMapper roleMapper;

    @GetMapping("/register")
    public String registerPage(Model model) {
        return createUserForm(model, new UserDto(), "register", "register");
    }

    @PostMapping("/register")
    public String createAccount(@Valid @ModelAttribute UserDto userDto,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        model.addAttribute("module", "register");
        if(checkErrors(userDto, bindingResult)) {
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
        return "redirect:/users/register?success=true";
    }

    @GetMapping
    public String showAllUsers(Model model) {
        model.addAttribute("module", "users");
        return "user-list-page";
    }

    @GetMapping("/add")
    public String addUser(Model model) {
        return createUserForm(model, new UserDto(), "add-user-page", "users");
    }

    @PostMapping("/save")
    public String insertUser(@Valid @ModelAttribute UserDto userDto,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        model.addAttribute("allRoles", roleService.getAllRoles());
        if(checkErrors(userDto, bindingResult)) {
            return "add-user-page";
        }

        User user = userMapper.toEntity(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword().trim()));
        user.setRole(roleMapper.toEntity(userDto.getRole()));
        userService.saveAccount(user);
        redirectAttributes.addFlashAttribute("success", MessageHelper.getMessage("user.success.create"));
        return "redirect:/users";
    }

    @GetMapping("/{id}/edit")
    public String editBook(Model model, @PathVariable int id) {
        User user = userService.getUserById(id);
        return createUserForm(model, userMapper.toDto(user), "edit-user-page", "users");
    }

    @PostMapping("/update")
    public String UpdateUser(@Valid @ModelAttribute("userDto") UserUpdateDto userUpdateDto, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("allRoles", roleService.getAllRoles());
            return "edit-user-page";
        }

        try {
            userService.updateUser(userUpdateDto);
        } catch (AlreadyExistsException e) {
            if (e.getMessage().contains("Username")) {
                bindingResult.rejectValue("username", "user.username.error.alreadyExists");
            } else {
                bindingResult.rejectValue("email", "user.email.error.alreadyExists");
            }
            return createUserForm(model, userUpdateDto, "edit-user-page", "users");
        }
        redirectAttributes.addFlashAttribute("success", MessageHelper.getMessage("user.success.edit"));
        return "redirect:/users";
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@AuthenticationPrincipal UserDetailsImpl currentUser,
                             @PathVariable int id,
                             RedirectAttributes redirectAttributes) {
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
        model.addAttribute("userDto", userMapper.toChangePasswordDto(user));
        return "user-change-password-page";
    }

    @PostMapping("/save-password")
    public String SavePassword(@Valid @ModelAttribute("userDto") ChangePasswordDto changePasswordDto,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) return "user-change-password-page";

        try {
            if(changePasswordDto.getId() != null) {
                User user = userService.getChangePasswordUser(passwordEncoder, changePasswordDto);
                user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword().trim()));
                userService.saveAccount(user);
                redirectAttributes.addFlashAttribute("success", "You have successfully updated " + user.getUsername() +  "'s password!");
            }
        } catch (MisMatchException e) {
            bindingResult.rejectValue("confirmPassword", "user.password.error.mismatch", e.getMessage());
            return "user-change-password-page";
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("oldPassword", "user.oldPassword.error.invalid", e.getMessage());
            return "user-change-password-page";
        }
        return "redirect:/users";
    }

    private String createUserForm(Model model, Object dto, String view, String module) {
        model.addAttribute("userDto", dto);
        model.addAttribute("module", module);
        model.addAttribute("allRoles", roleService.getAllRoles());
        return view;
    }

    private boolean checkErrors(UserDto userDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return true;

        if (userService.findByUsername(userDto.getUsername().trim()) != null) {
            bindingResult.rejectValue("username", "user.username.error.alreadyExists");
        }

        if(userService.findByEmail(userDto.getEmail().trim()) != null) {
            bindingResult.rejectValue("email", "user.email.error.alreadyExists", "error.alreadyExists");
        }

        if(!Objects.equals(userDto.getPassword().trim(), userDto.getConfirmPassword().trim())) {
            bindingResult.rejectValue("confirmPassword", "user.password.error.mismatch", "error.mismatch");
        }

        return bindingResult.hasErrors();
    }
}
