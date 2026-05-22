package com.sip.book_shop.api.controller;

import com.sip.book_shop.api.request.*;
import com.sip.book_shop.api.response.ApiResponse;
import com.sip.book_shop.api.response.PageUserResponse;
import com.sip.book_shop.api.response.SingleUserResponse;
import com.sip.book_shop.api.response.UserResponse;
import com.sip.book_shop.api.service.UserApiService;
import com.sip.book_shop.handler.ResponseHandler;
import com.sip.book_shop.model.Author;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserApiController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserApiService userApiService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterInfoRequest request) {
        userApiService.register(request);
        var response = ResponseHandler.successMsgResponse("Registered successfully!");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, String>>> login(@Valid @RequestBody LoginInfoRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            var tokenData = userApiService.getLoginData(request.getUsername());
            var response = ResponseHandler.successResponse("logged in successfully!", tokenData);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BadCredentialsException | UsernameNotFoundException e) {
            throw new BadCredentialsException("Invalid username or password!");
        }
    }

    @GetMapping
    public ResponseEntity<PageUserResponse> getAllUsers(@RequestParam(defaultValue = "1") int page,
                                                        @RequestParam(defaultValue = "10") int size,
                                                        @RequestParam(defaultValue = "asc") String sortDir,
                                                        @RequestParam(defaultValue = "") String searchValue) {
        var response = userApiService.getAll(page, size, sortDir, searchValue);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleUserResponse> getUserById(@PathVariable int id) {
        var response = userApiService.getById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Void>> addNewUser(@Valid @RequestBody AddUserInfoRequest request) {
        userApiService.addNew(request);
        var response = ResponseHandler.successMsgResponse("Created user successfully!");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateUser(@PathVariable int id,
                                                        @Valid @RequestBody UpdateUserRequest request) {
        userApiService.update(id, request);
        var response = ResponseHandler.successMsgResponse("Updated successfully!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable int id) {
        userApiService.delete(id);
        var response = ResponseHandler.successMsgResponse("Deleted successfully!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@PathVariable int id,
                                                            @Valid @RequestBody ChangePasswordRequest request) {
        userApiService.changePassword(request, id);
        var response = ResponseHandler.successMsgResponse("Updated password successfully!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}