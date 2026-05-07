package com.sip.book_shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WelcomeController {

    @GetMapping
    public static String index() {
        return "redirect:/welcome";
    }

    @GetMapping("/welcome")
    public static String welcome() {
        return "welcome";
    }

    @GetMapping("/welcome/")
    public static String redirectWelcome() {
        return "redirect:/welcome";
    }
}
