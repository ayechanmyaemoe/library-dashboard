package com.sip.book_shop.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MisMatchException extends RuntimeException {

    public MisMatchException(String message) {
        super(message);
    }
}
