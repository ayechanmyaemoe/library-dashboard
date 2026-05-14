package com.sip.book_shop.handler;

import com.sip.book_shop.config.UserDetailsImpl;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.nio.file.AccessDeniedException;
import java.util.Objects;

@ControllerAdvice
public class GlobalHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDeniedException(AccessDeniedException e) {
        return "redirect:/error/403";
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public String httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return "redirect:/error/403";
    }

    @ModelAttribute
    public void addAttributes(Model model, @AuthenticationPrincipal UserDetailsImpl currentUser) {
        if (currentUser != null) {
            model.addAttribute("username", currentUser.getUsername());

            boolean isAdmin = currentUser.getAuthorities().stream()
                    .anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_ADMIN"));
            model.addAttribute("isAdmin", isAdmin);
        }
    }
}
