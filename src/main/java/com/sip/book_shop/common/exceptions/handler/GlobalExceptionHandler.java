package com.sip.book_shop.common.exceptions.handler;

import com.sip.book_shop.vo.ApiResponse;
import com.sip.book_shop.common.exceptions.AlreadyExistsException;
import com.sip.book_shop.common.exceptions.MisMatchException;
import com.sip.book_shop.common.exceptions.NotAllowedException;
import com.sip.book_shop.common.exceptions.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentialsException(BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.badRequest(e.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserNotFoundException(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.notFound(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.badRequest("Request validation failed!", errors));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(BindException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.badRequest("Request validation failed!", errors));
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleAlreadyExistsException(AlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.conflict(e.getMessage()));
    }

    @ExceptionHandler(NotAllowedException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotAllowedException(NotAllowedException e) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(ApiResponse.notAllowed(e.getMessage()));
    }

    @ExceptionHandler(MisMatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleMisMatchException(MisMatchException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.badRequest(e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.badRequest(e.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String errorMessage = "Invalid request body format.";

        if (ex.getCause() != null && ex.getCause().getMessage().contains("java.time.LocalDate")) {
            errorMessage = "Invalid birthdate format provided. Please use the 'dd/MM/yyyy' format.";
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.badRequest(errorMessage));
    }
}
