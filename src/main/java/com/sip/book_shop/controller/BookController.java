package com.sip.book_shop.controller;

import com.sip.book_shop.dto.BookDto;
import com.sip.book_shop.model.Author;
import com.sip.book_shop.model.Book;
import com.sip.book_shop.model.Category;
import com.sip.book_shop.service.AuthorService;
import com.sip.book_shop.service.BookService;
import com.sip.book_shop.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/")
    public String index() {
        return "redirect:/books";
    }

    @GetMapping
    public String getAllBooks() {
        return "book-list-page";
    }

    @GetMapping("/add")
    public String addBook(Model model) {
        model.addAttribute("book", new BookDto());

        List<Author> authors = authorService.getAllAuthors();
        model.addAttribute("allAuthors", authors);

        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("allCategories", categories);
        return "add-book-page";
    }

    @PostMapping("/save")
    public String insertOrUpdateBook(@Valid @ModelAttribute("book") BookDto bookDto, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {
            List<Author> authors = authorService.getAllAuthors();
            model.addAttribute("allAuthors", authors);
            List<Category> categories = categoryService.getAllCategories();
            model.addAttribute("allCategories", categories);
            if(bookDto.getId() != null) {
                return "edit-book-page";
            } else {
                return"add-book-page";
            }
        }

        Book book;
        if(bookDto.getId() != null) {
            book = bookService.getBookById(bookDto.getId());
        } else {
            book = new Book();
        }
        book.setTitle(bookDto.getTitle().trim());
        book.setPublishedYear(Integer.parseInt(bookDto.getPublishedYear()));
        book.setAuthor(bookDto.getAuthor());
        book.setCategory(bookDto.getCategory());
        bookService.saveBook(book);
        return "redirect:/books";
    }

    @GetMapping("/{id}/edit")
    public String editBook(Model model, @PathVariable int id) {
        Book book = bookService.getBookById(id);
        model.addAttribute("book", book);

        List<Author> authors = authorService.getAllAuthors();
        model.addAttribute("allAuthors", authors);

        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("allCategories", categories);
        return "edit-book-page";
    }

    @DeleteMapping("/{id}")
    public String deleteBook(@PathVariable int id) {
        bookService.deleteBookById(id);
        return "redirect:/books";
    }
}


