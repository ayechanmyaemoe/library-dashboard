package com.sip.book_shop.controller;

import com.sip.book_shop.config.UserDetailsImpl;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WelcomeController {

    @GetMapping
    public static String index() {
        return "redirect:/welcome";
    }

    @GetMapping("/welcome")
    public static String welcome(@AuthenticationPrincipal UserDetailsImpl currentUser, Model model) {
        model.addAttribute("username", currentUser.getUsername());
        model.addAttribute("module", "home");
        return "welcome";
    }

    @GetMapping("/welcome/")
    public static String redirectWelcome() {
        return "redirect:/welcome";
    }
}
