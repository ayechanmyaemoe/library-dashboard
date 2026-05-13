package com.sip.book_shop.controller;

import com.sip.book_shop.dto.AuthorDto;
import com.sip.book_shop.mapper.AuthorMapper;
import com.sip.book_shop.model.Author;
import com.sip.book_shop.service.AuthorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/authors")
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    @Autowired
    private AuthorMapper authorMapper;

    @GetMapping("/")
    public String index() {
        return "redirect:/authors";
    }

    @GetMapping
    public String getAllBooks(Model model) {
        model.addAttribute("module", "authors");
        return "author-list-page";
    }

    @GetMapping("/add")
    public String addAuthor(Model model) {
        model.addAttribute("author", new AuthorDto());
        return "add-author-page";
    }

    @PostMapping("/save")
    public String insertOrUpdateAuthor(@Valid @ModelAttribute("author") AuthorDto authorDto, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            if(authorDto.getId() != null) {
                return "edit-author-page";
            } else {
                return "add-author-page";
            }
        }

        Author author;
        if(authorDto.getId() != null) {
            author = authorService.getAuthorById(authorDto.getId());
        } else {
            author = authorMapper.toEntity(authorDto);
        }
        authorService.saveAuthor(author);
        return "redirect:/authors";
    }

    @GetMapping("/{id}/edit")
    public String editAuthor(Model model, @PathVariable int id) {
        Author author = authorService.getAuthorById(id);
        model.addAttribute("author", author);
        return "edit-author-page";
    }

    @DeleteMapping("/{id}")
    public String deleteAuthor(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            authorService.deleteAuthorById(id);
            return "redirect:/authors";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/authors";
    }

}
