package com.sip.book_shop.api.controller;

import com.sip.book_shop.api.request.AuthorRequest;
import com.sip.book_shop.api.response.ApiResponse;
import com.sip.book_shop.api.response.PageAuthorResponse;
import com.sip.book_shop.api.service.AuthorApiService;
import com.sip.book_shop.handler.ResponseHandler;
import com.sip.book_shop.model.Author;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/authors")
public class AuthorApiController {

    @Autowired
    private AuthorApiService authorApiService;

    @GetMapping
    public ResponseEntity<PageAuthorResponse> getAllAuthors(@RequestParam(defaultValue = "1") int page,
                                                            @RequestParam(defaultValue = "10") int size,
                                                            @RequestParam(defaultValue = "asc") String sortDir,
                                                            @RequestParam(defaultValue = "") String searchValue) {
        var response = authorApiService.getAll(page, size, sortDir, searchValue);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Author> getAuthorById(@PathVariable int id) {
        var response = authorApiService.getById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> addNewAuthor(@Valid @RequestBody AuthorRequest request) {
        authorApiService.addNew(request);
        var response = ResponseHandler.successMsgResponse("Created author successfully!");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateAuthor(@Valid @RequestBody AuthorRequest request,
                                                          @PathVariable int id) {
        authorApiService.update(request, id);
        var response = ResponseHandler.successMsgResponse("Updated author successfully!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAuthor(@PathVariable int id) {
        authorApiService.delete(id);
        var response = ResponseHandler.successMsgResponse("Deleted author successfully!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
