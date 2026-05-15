package com.sip.book_shop.service;

import com.sip.book_shop.dto.UserUpdateDto;
import com.sip.book_shop.exception.AlreadyExistsException;
import com.sip.book_shop.helper.MessageHelper;
import com.sip.book_shop.mapper.RoleMapper;
import com.sip.book_shop.model.User;
import com.sip.book_shop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleMapper roleMapper;

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void saveAccount(User user) {
        userRepository.save(user);
    }

    public void updateUser(UserUpdateDto updateDto) {
        User user = userRepository.findById(updateDto.getId())
                .orElseThrow(() -> new IllegalArgumentException(MessageHelper.getMessage("user.error.notFound")));

        if (!user.getUsername().equals(updateDto.getUsername())) {
            if (userRepository.existsByUsername(updateDto.getUsername())) {
                throw new AlreadyExistsException("Username");
            }
            user.setUsername(updateDto.getUsername().trim());
        }

        if (!user.getEmail().equals(updateDto.getEmail())) {
            if (userRepository.existsByEmail(updateDto.getEmail())) {
                throw new AlreadyExistsException("Email");
            }
            user.setEmail(updateDto.getEmail().trim());
        }

        user.setRole(roleMapper.toEntity(updateDto.getRole()));
        userRepository.save(user);
    }

    public Page<User> findPaginated(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Page<User> searchUsers(String searchValue, Pageable pageable) {
        return userRepository.searchByKeyword(searchValue, pageable);
    }

    public int countAllUsers() {
        return userRepository.findAll().size();
    }

    public User getUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(MessageHelper.getMessage("user.error.notFound")));
    }

    public void deleteUserById(String currentUsername, int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(MessageHelper.getMessage("user.error.notFound")));

        if(currentUsername.equals(user.getUsername())) {
            throw new IllegalStateException(MessageHelper.getMessage("user.error.denyDeletion"));
        }
        userRepository.deleteById(user.getId());
    }
}
