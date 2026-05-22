package com.sip.book_shop.api.controller;

import com.sip.book_shop.api.request.RoleRequest;
import com.sip.book_shop.api.response.ApiResponse;
import com.sip.book_shop.api.response.PageRoleResponse;
import com.sip.book_shop.api.service.RoleApiService;
import com.sip.book_shop.handler.ResponseHandler;
import com.sip.book_shop.model.Category;
import com.sip.book_shop.model.Role;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleApiController {

    @Autowired
    private RoleApiService roleApiService;

    @GetMapping
    public ResponseEntity<PageRoleResponse> getAllRoles(@RequestParam(defaultValue = "1") int page,
                                                        @RequestParam(defaultValue = "10") int size,
                                                        @RequestParam(defaultValue = "asc") String sortDir,
                                                        @RequestParam(defaultValue = "") String searchValue) {
        var response = roleApiService.getAll(page, size, sortDir, searchValue);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable int id) {
        var response = roleApiService.getById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Void>> addNewRole(@Valid @RequestBody RoleRequest request) {
        roleApiService.addNew(request);
        var response = ResponseHandler.successMsgResponse("Created role successfully!");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateRole(@Valid @RequestBody RoleRequest request,
                                                        @PathVariable int id) {
        roleApiService.update(request, id);
        var response = ResponseHandler.successMsgResponse("Updated role successfully!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable int id) {
        roleApiService.delete(id);
        var response = ResponseHandler.successMsgResponse("Deleted role successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
