package com.sip.book_shop.handler;

import com.sip.book_shop.api.response.ApiResponse;

public class ResponseHandler {

    public static <T> ApiResponse<T> successResponse(String message, T data) {
        return ApiResponse.<T>builder()
                .message(message)
                .data(data)
                .build();
    }

    public static ApiResponse<Void> successMsgResponse(String message) {
        return ApiResponse.<Void>builder()
                .message(message)
                .build();
    }
}
