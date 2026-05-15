package com.sip.book_shop.controller;

import com.sip.book_shop.dto.BookDto;
import com.sip.book_shop.exception.AlreadyExistsException;
import com.sip.book_shop.helper.MessageHelper;
import com.sip.book_shop.mapper.AuthorMapper;
import com.sip.book_shop.mapper.BookMapper;
import com.sip.book_shop.mapper.CategoryMapper;
import com.sip.book_shop.model.Book;
import com.sip.book_shop.service.AuthorService;
import com.sip.book_shop.service.BookService;
import com.sip.book_shop.service.CategoryService;
import com.sun.jdi.request.DuplicateRequestException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private AuthorMapper authorMapper;

    @Autowired
    private CategoryMapper categoryMapper;

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
        return createForm(model, new BookDto(), "add-book-page");
    }

    @PostMapping("/save")
    public String insertOrUpdateBook(@Valid @ModelAttribute BookDto bookDto,
                                     BindingResult bindingResult,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()) {
            return createForm(model, bookDto, bookDto.getId() != null ? "edit-book-page" : "add-book-page");
        }

        try {
            boolean isUpdate = bookDto.getId() != null;
            Book book;
            if(isUpdate) {
                book = bookService.getBookById(bookDto.getId());
                book.setTitle(bookDto.getTitle().trim());
                book.setAuthor(authorMapper.toEntity(bookDto.getAuthor()));
                book.setCategory(categoryMapper.toEntity(bookDto.getCategory()));
                book.setPublishedYear(Integer.parseInt(bookDto.getPublishedYear()));
            } else {
                book = bookMapper.toEntity(bookDto);
            }
            bookService.saveBook(book);
            String messageKey = isUpdate ? "book.success.edit" : "book.success.create";
            redirectAttributes.addFlashAttribute("success", MessageHelper.getMessage(messageKey));
        } catch (AlreadyExistsException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/books";
    }

    @GetMapping("/{id}/edit")
    public String editBook(Model model, @PathVariable int id) {
        Book book = bookService.getBookById(id);
        return createForm(model, bookMapper.toDto(book), "edit-book-page");
    }

    @DeleteMapping("/{id}")
    public String deleteBook(@PathVariable int id) {
        bookService.deleteBookById(id);
        return "redirect:/books";
    }

    private String createForm(Model model, BookDto dto, String view) {
        model.addAttribute("bookDto", dto);
        model.addAttribute("allAuthors", authorService.getAllAuthors());
        model.addAttribute("allCategories", categoryService.getAllCategories());
        return view;
    }
}


