package com.sip.book_shop.api.controller;

import com.sip.book_shop.api.request.CategoryRequest;
import com.sip.book_shop.api.response.ApiResponse;
import com.sip.book_shop.api.response.PageCategoryResponse;
import com.sip.book_shop.api.service.CategoryApiService;
import com.sip.book_shop.handler.ResponseHandler;
import com.sip.book_shop.model.Category;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryApiController {

    @Autowired
    private CategoryApiService categoryApiService;

    @GetMapping
    public ResponseEntity<PageCategoryResponse> getAllCategories(@RequestParam(defaultValue = "1") int page,
                                                                 @RequestParam(defaultValue = "10") int size,
                                                                 @RequestParam(defaultValue = "asc") String sortDir) {
        var response = categoryApiService.getAll(page, size, sortDir);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> addNewCategory(@Valid @RequestBody CategoryRequest request) {
        categoryApiService.addNew(request);
        var response = ResponseHandler.successMsgResponse("Created category successfully!");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateCategory(@Valid @RequestBody CategoryRequest request,
                                                            @PathVariable int id) {
        categoryApiService.update(request, id);
        var response = ResponseHandler.successMsgResponse("Updated category successfully!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable int id) {
        categoryApiService.delete(id);
        var response = ResponseHandler.successMsgResponse("Deleted category successfully!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
