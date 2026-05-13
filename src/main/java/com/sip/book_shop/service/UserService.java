package com.sip.book_shop.service;

import com.sip.book_shop.model.Book;
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

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void saveAccount(User user) {
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
                .orElseThrow(() -> new IllegalArgumentException("There is no user with such id."));
    }

    public void deleteUserById(String currentUsername, int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("There is no user with such id"));

        if(currentUsername.equals(user.getUsername())) {
            throw new IllegalStateException("Sorry! You can't delete current logged in user! Please ask other admins to perform this function.");
        }
        userRepository.deleteById(user.getId());
    }
}
