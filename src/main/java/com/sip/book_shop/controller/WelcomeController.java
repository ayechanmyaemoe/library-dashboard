package com.sip.book_shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WelcomeController {

    @GetMapping
    public String index() {
        return "redirect:/welcome";
    }

    @GetMapping("/welcome")
    public String welcome(Model model) {
        model.addAttribute("module", "home");
        return "welcome";
    }

    @GetMapping("/welcome/")
    public String redirectWelcome() {
        return "redirect:/welcome";
    }
}
