package com.sip.book_shop.api.service;

import com.sip.book_shop.api.request.AddUserInfoRequest;
import com.sip.book_shop.api.request.ChangePasswordRequest;
import com.sip.book_shop.api.request.RegisterInfoRequest;
import com.sip.book_shop.api.request.UpdateUserRequest;
import com.sip.book_shop.api.response.PageUserResponse;
import com.sip.book_shop.api.response.SingleUserResponse;
import com.sip.book_shop.api.response.UserResponse;
import com.sip.book_shop.config.JwtService;
import com.sip.book_shop.exception.AlreadyExistsException;
import com.sip.book_shop.exception.MisMatchException;
import com.sip.book_shop.exception.NotAllowedException;
import com.sip.book_shop.exception.NotFoundException;
import com.sip.book_shop.mapper.UserMapper;
import com.sip.book_shop.model.Author;
import com.sip.book_shop.model.User;
import com.sip.book_shop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserApiService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserMapper userMapper;

    public void register(RegisterInfoRequest request) {
        User user = userMapper.toEntity(request);
        checkConflict(user.getUsername(), user.getEmail());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public void addNew(AddUserInfoRequest request) {
        User user = userMapper.toEntity(request);
        checkConflict(user.getUsername(), user.getEmail());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public Map<String, String> getLoginData(String username) {
        String token = jwtService.generateToken(username);
        Map<String, String> tokenData = new HashMap<>();
        tokenData.put("token", token);
        return tokenData;
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public PageUserResponse getAll(int page, int size, String sortDir, String searchValue) {
        Page<User> users;
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by("id").ascending() : Sort.by("id").descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        if(searchValue != null && !searchValue.isEmpty()) {
            users = searchUsers(searchValue, pageable);
        } else {
            users = userRepository.findAll(pageable);
        }

        List<UserResponse> responseUsers = new ArrayList<>();
        for(User user: users) {
            responseUsers.add(userMapper.toResponse(user));
        }

        return PageUserResponse.builder()
                .page(page)
                .totalPage(users.getTotalPages())
                .totalDataCount(userRepository.findAll().size())
                .users(responseUsers)
                .build();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public SingleUserResponse getById(int id) {
        User user = getExistingUser(id);
        return userMapper.toSingleResponse(user);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void update(int id, UpdateUserRequest request) {
        User updateUser = userMapper.toEntity(request);
        User existingUser = getExistingUser(id);
        if(!Objects.equals(existingUser.getUsername(), updateUser.getUsername())) {
            checkUsername(updateUser.getUsername());
        } else if(!Objects.equals(existingUser.getEmail(), updateUser.getEmail())) {
            checkEmail(updateUser.getEmail());
        }
        updateUser.setId(id);
        updateUser.setPassword(existingUser.getPassword());
        updateUser.setEnabled(true);
        userRepository.save(updateUser);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void delete(int id) {
        User existingUser = getExistingUser(id);
        Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String currentUsername = "";

        if(principle != null) {
            if(principle instanceof UserDetails) {
                currentUsername = ((UserDetails) principle).getUsername();
            } else {
                currentUsername = principle.toString();
            }
        }

        if(existingUser.getUsername().equals(currentUsername)) {
            throw new NotAllowedException("You can't delete current logged in user! Please ask other admins to perform this function.");
        }
        userRepository.deleteById(existingUser.getId());
    }

    public void changePassword(ChangePasswordRequest request, int id) {
        User existingUser = getExistingUser(id);
        if(!passwordEncoder.matches(request.getOldPassword().trim(), existingUser.getPassword())) {
            throw new MisMatchException("Invalid old password!");
        }
        User updateUser = new User();
        updateUser.setPassword(passwordEncoder.encode(request.getNewPassword().trim()));
        updateUser.setId(existingUser.getId());
        updateUser.setUsername(existingUser.getUsername());
        updateUser.setEmail(existingUser.getEmail());
        updateUser.setRole(existingUser.getRole());
        userRepository.save(updateUser);
    }

    public User getExistingUser(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("There is no user with such id."));
    }

    private void checkConflict(String username, String email) {
        checkUsername(username);

        checkEmail(email);
    }

    private void checkUsername(String username) {
        User sameUser = userRepository.findByUsername(username);
        if(sameUser != null) {
            throw new AlreadyExistsException("Username already existed!");
        }
    }

    private void checkEmail(String email) {
        User sameEmailUser = userRepository.findByEmail(email);
        if(sameEmailUser != null) {
            throw new AlreadyExistsException("Email already registered!");
        }
    }

    public Page<User> searchUsers(String searchValue, Pageable pageable) {
        return userRepository.searchByKeyword(searchValue, pageable);
    }

}
