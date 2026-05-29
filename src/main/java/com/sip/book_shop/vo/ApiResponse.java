package com.sip.book_shop.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.sip.book_shop.constant.Constant;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "status", "timestamp", "message", "data" })
public class ApiResponse<T> {

    private int status;

    @JsonFormat(pattern = "dd-MM-yyy hh:mm:ss a", timezone = "Asia/Singapore")
    private LocalDateTime timestamp;

    private String message;

    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .status(Constant.HTTP_STATUS_SUCCESS)
                .message("success")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .status(Constant.HTTP_STATUS_SUCCESS)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ApiResponse<Void> success(String message) {
        return ApiResponse.<Void>builder()
                .status(Constant.HTTP_STATUS_SUCCESS)
                .message(message)
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ApiResponse<Void> created(String message) {
        return ApiResponse.<Void>builder()
                .status(Constant.HTTP_STATUS_CREATED)
                .message(message)
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> unauthorized(String message) {
        return ApiResponse.<T>builder()
                .status(Constant.HTTP_STATUS_UNAUTHORIZED)
                .message(message)
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> notFound(String message) {
        return ApiResponse.<T>builder()
                .status(Constant.HTTP_STATUS_NOT_FOUND)
                .message(message)
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> badRequest(String message) {
        return ApiResponse.<T>builder()
                .status(Constant.HTTP_STATUS_BAD_REQUEST)
                .message(message)
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> badRequest(String message, T data) {
        return ApiResponse.<T>builder()
                .status(Constant.HTTP_STATUS_BAD_REQUEST)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> conflict(String message) {
        return ApiResponse.<T>builder()
                .status(Constant.HTTP_STATUS_CONFLICT)
                .message(message)
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> notAllowed(String message) {
        return ApiResponse.<T>builder()
                .status(Constant.HTTP_STATUS_NOT_ACCEPTABLE)
                .message(message)
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
