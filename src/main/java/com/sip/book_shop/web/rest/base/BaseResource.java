package com.sip.book_shop.web.rest.base;

import com.sip.book_shop.vo.ApiResponse;
import com.sip.book_shop.common.vo.NzDataTableInput;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;

public interface BaseResource<P, S, I, U> {

    @PostMapping("/search")
    ResponseEntity<P> findAll(@Valid @RequestBody NzDataTableInput dataTableInput);

    @GetMapping("/{id}")
    ResponseEntity<S> findById(@PathVariable int id);

    @PostMapping
    ResponseEntity<ApiResponse<Void>> create(@Valid @RequestBody I request) throws BindException;

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> update(@Valid @RequestBody U request, @PathVariable int id) throws BindException;

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> delete(@PathVariable int id);
}
