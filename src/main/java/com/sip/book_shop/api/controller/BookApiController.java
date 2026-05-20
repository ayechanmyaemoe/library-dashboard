package com.sip.book_shop.api.controller;

import com.sip.book_shop.api.request.AuthorRequest;
import com.sip.book_shop.api.request.BookRequest;
import com.sip.book_shop.api.response.ApiResponse;
import com.sip.book_shop.api.response.BookResponse;
import com.sip.book_shop.api.response.PageBookResponse;
import com.sip.book_shop.api.service.BookApiService;
import com.sip.book_shop.handler.ResponseHandler;
import com.sip.book_shop.model.Author;
import com.sip.book_shop.model.Book;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookApiController {

    @Autowired
    private BookApiService bookApiService;

    @GetMapping
    public ResponseEntity<PageBookResponse> getAllBooks(@RequestParam(defaultValue = "1") int page,
                                                        @RequestParam(defaultValue = "10") int size,
                                                        @RequestParam(defaultValue = "asc") String sortDir) {
        var response = bookApiService.getAll(page, size, sortDir);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> addNewBook(@Valid @RequestBody BookRequest request) {
        bookApiService.addNew(request);
        var response = ResponseHandler.successMsgResponse("Created book successfully!");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateBook(@Valid @RequestBody BookRequest request,
                                                          @PathVariable int id) {
        bookApiService.update(request, id);
        var response = ResponseHandler.successMsgResponse("Updated book successfully!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable int id) {
        bookApiService.delete(id);
        var response = ResponseHandler.successMsgResponse("Deleted book successfully!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
