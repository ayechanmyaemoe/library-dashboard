package com.sip.book_shop.service;

import com.sip.book_shop.common.query.utils.QueryHelper;
import com.sip.book_shop.web.dto.UserDTO;
import com.sip.book_shop.web.dto.mapper.UserMapper;
import com.sip.book_shop.entities.queryCriteria.UserQueryCriteria;
import com.sip.book_shop.security.authentication.jwt.TokenProvider;
import com.sip.book_shop.security.authentication.jwt.TokenResponse;
import com.sip.book_shop.security.dto.AuthenticationInfo;
import com.sip.book_shop.security.dto.CustomUserDetails;
import com.sip.book_shop.vo.*;
import com.sip.book_shop.common.exception.NotAllowedException;
import com.sip.book_shop.common.exception.NotFoundException;
import com.sip.book_shop.entities.User;
import com.sip.book_shop.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserApiService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final UserMapper userMapper;

    public void register(RegisterUserRequest request) throws BindException {
        User user = userMapper.toEntity(request);
        checkConflict(request, "registerUserRequest", request.getUsername(), request.getEmail());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public void addNew(AddUserRequest request) throws BindException {
        User user = userMapper.toEntity(request);
        checkConflict(request, "addUserRequest", request.getUsername(), request.getEmail());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public AuthenticationInfo getLoginData(Authentication authentication) {
        TokenResponse tokenResponse = tokenProvider.createToken(authentication);
        return buildAuthenticationInfo(tokenResponse.getAccessToken(), tokenResponse.getRefreshToken());
    }

    public AuthenticationInfo refreshToken(String refreshToken) {
        if (!tokenProvider.validateJwtToken(refreshToken)) {
            throw new RuntimeException("Refresh token is expired or invalid.");
        }

        UserDetails userDetails = tokenProvider.getPrincipalFromToken(refreshToken);
        String newAccessToken = tokenProvider.generateAccessTokenFromUserDetails(userDetails);
        return buildAuthenticationInfo(newAccessToken, refreshToken);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Page<UserDTO> getAllResult(UserQueryCriteria criteria) {
        Specification<User> specification = (root, cq, cb) -> QueryHelper.getPredicate(root, criteria, cq, cb);
        Page<User> pageUsers = userRepository.findAll(specification, criteria.getPageable());

        List<UserDTO> responseRoles = pageUsers.stream().map(userMapper::toDto)
                .toList();

        return new PageImpl<>(responseRoles, pageUsers.getPageable(), pageUsers.getTotalElements());
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<UserDTO> getAll() {
        List<UserDTO> userDTOs = new ArrayList<>();
        List<User> users = userRepository.findAll();
        for(User user: users) {
            userDTOs.add(userMapper.toDto(user));
        }
        return userDTOs;
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public UserDTO findById(int id) {
        return userMapper.toDto(getExistingUser(id));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void update(int id, UpdateUserRequest request) throws BindException {
        User updateUser = userMapper.toEntity(request);
        User existingUser = getExistingUser(id);
        if(!Objects.equals(existingUser.getUsername().toLowerCase(), updateUser.getUsername().toLowerCase())) {
            checkUsername(request, "updateUserRequest", request.getUsername());
        } else if(!Objects.equals(existingUser.getEmail(), updateUser.getEmail())) {
            checkEmail(request, "updateUserRequest", request.getEmail());
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

    public void changePassword(ChangePasswordRequest request, int id) throws BindException {
        User existingUser = getExistingUser(id);
        if(!passwordEncoder.matches(request.getOldPassword().trim(), existingUser.getPassword())) {
            BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(request, "changePasswordRequest");
            bindingResult.rejectValue("oldPassword", "invalid", "Invalid old password!");
            throw new BindException(bindingResult);
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

    private void checkConflict(Object target, String objectName, String username, String email) throws BindException {
        checkUsername(target, objectName, username);

        checkEmail(target, objectName, email);
    }

    private void checkUsername(Object request, String objectName, String username) throws BindException {
        User sameUser = userRepository.findByUsername(username);
        if(sameUser != null) {
            BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(request, objectName);
            bindingResult.rejectValue("username", "duplicate", "Username already existed!");
            throw new BindException(bindingResult);
        }
    }

    private void checkEmail(Object request, String objectName, String email) throws BindException {
        User sameEmailUser = userRepository.findByEmail(email);
        if(sameEmailUser != null) {
            BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(request, objectName);
            bindingResult.rejectValue("email", "duplicate", "Email already existed!");
            throw new BindException(bindingResult);
        }
    }

    public AuthenticationInfo buildAuthenticationInfo(String accessToken, String refreshToken) {
        CustomUserDetails userDetails = tokenProvider.getPrincipalFromToken(refreshToken);
        return AuthenticationInfo.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(UserDTO.builder().username(userDetails.getUsername()).email(userDetails.getEmail()).build())
                .grantedAuthorities(userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .build();
    }

}
