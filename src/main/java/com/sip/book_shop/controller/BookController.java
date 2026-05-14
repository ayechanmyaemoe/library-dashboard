package com.sip.book_shop.controller;

import com.sip.book_shop.dto.BookDto;
import com.sip.book_shop.mapper.BookMapper;
import com.sip.book_shop.model.Author;
import com.sip.book_shop.model.Book;
import com.sip.book_shop.model.Category;
import com.sip.book_shop.service.AuthorService;
import com.sip.book_shop.service.BookService;
import com.sip.book_shop.service.CategoryService;
import com.sun.jdi.request.DuplicateRequestException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/books")
@Slf4j
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BookMapper bookMapper;

    @GetMapping("/")
    public String index() {
        return "redirect:/books";
    }

    @GetMapping
    public String getAllBooks(Model model) {
        model.addAttribute("module", "books");
        return "book-list-page";
    }

    @GetMapping("/add")
    public String addBook(Model model) {
        model.addAttribute("bookDto", new BookDto());
        model.addAttribute("allAuthors", authorService.getAllAuthors());
        model.addAttribute("allCategories", categoryService.getAllCategories());
        return "add-book-page";
    }

    @PostMapping("/save")
    public String insertOrUpdateBook(@Valid @ModelAttribute BookDto bookDto, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("allAuthors", authorService.getAllAuthors());
            model.addAttribute("allCategories", categoryService.getAllCategories());
            if(bookDto.getId() != null) {
                return "edit-book-page";
            } else {
                return"add-book-page";
            }
        }

        try {
            Book book;
            if(bookDto.getId() != null) {
                book = bookService.getBookById(bookDto.getId());
                book.setTitle(bookDto.getTitle().trim());
                book.setAuthor(bookDto.getAuthor());
                book.setCategory(bookDto.getCategory());
            } else {
                book = bookMapper.toEntity(bookDto);
            }
            book.setPublishedYear(Integer.parseInt(bookDto.getPublishedYear()));
            bookService.saveBook(book);
        } catch (DuplicateRequestException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/books";
    }

    @GetMapping("/{id}/edit")
    public String editBook(Model model, @PathVariable int id) {
        Book book = bookService.getBookById(id);
        BookDto bookDto = bookMapper.toDto(book);
        model.addAttribute("bookDto", bookDto);
        model.addAttribute("allAuthors", authorService.getAllAuthors());
        model.addAttribute("allCategories", categoryService.getAllCategories());
        return "edit-book-page";
    }

    @DeleteMapping("/{id}")
    public String deleteBook(@PathVariable int id) {
        bookService.deleteBookById(id);
        return "redirect:/books";
    }
}


