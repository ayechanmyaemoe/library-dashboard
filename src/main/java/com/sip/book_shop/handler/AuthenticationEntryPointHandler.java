package com.sip.book_shop.handler;

import com.sip.book_shop.api.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class AuthenticationEntryPointHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        var apiResponse = ApiResponse.<Void>builder()
                .message("Invalid or expired token! Please login again!")
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registeredModules();
        objectMapper.writeValue(response.getOutputStream(), apiResponse);
    }
}
