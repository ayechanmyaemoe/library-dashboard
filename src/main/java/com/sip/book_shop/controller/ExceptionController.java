package com.sip.book_shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/error")
public class ExceptionController {

    @GetMapping("/403")
    public String accessDenied(Model model) {
        model.addAttribute("module", "error");
        return "error/403";
    }
}
