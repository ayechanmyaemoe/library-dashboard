package com.sip.book_shop.handler;

import com.sip.book_shop.helper.MessageHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        String errorMessage = MessageHelper.getMessage("user.error.invalid");

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            errorMessage = MessageHelper.getMessage("user.error.required");
        } else if (exception.getMessage().equalsIgnoreCase("User is disabled")) {
            errorMessage = MessageHelper.getMessage("user.error.disabled");
        } else if (exception.getMessage().equalsIgnoreCase("User account is locked")) {
            errorMessage = MessageHelper.getMessage("user.error.locked");
        } else if (exception.getMessage().contains("User not found") || exception.getMessage().contains("Bad credentials")) {
            errorMessage = MessageHelper.getMessage("user.error.invalid");
        }

        response.sendRedirect("/login?error=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8));
    }
}