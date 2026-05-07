package com.sip.book_shop.controller.api;

import com.sip.book_shop.model.Author;
import com.sip.book_shop.model.Book;
import com.sip.book_shop.model.base.BaseEntity;
import com.sip.book_shop.service.AuthorService;
import com.sip.book_shop.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorService authorService;

    @GetMapping("/books")
    public Map<String, Object> getAllBookData(@RequestParam int draw,
                                              @RequestParam int start,
                                              @RequestParam int length,
                                              @RequestParam(value = "search[value]", required = false) String searchValue,
                                              @RequestParam(value = "order[0][column]", defaultValue = "0") int columnIndex,
                                              @RequestParam(value = "order[0][dir]", defaultValue = "asc") String sortDir) {
        Page<Book> pageBooks;

        String[] columnNames = {"id", "title", "author.name", "category.name", "publishedYear"};
        Pageable pageable = getPageable(start, length, columnNames, columnIndex, sortDir);

        if (searchValue != null && !searchValue.isEmpty()) {
            pageBooks = bookService.searchBooks(searchValue, pageable);
        } else {
            pageBooks = bookService.findPaginated(pageable);
        }

        return getResponse(
                draw,
                pageBooks.getTotalElements(),
                pageBooks.getTotalElements(),
                pageBooks.getContent()
        );
    }

    @GetMapping("/authors")
    public Map<String, Object> getAllAuthorData(@RequestParam int draw,
                                              @RequestParam int start,
                                              @RequestParam int length,
                                              @RequestParam(value = "search[value]", required = false) String searchValue,
                                              @RequestParam(value = "order[0][column]", defaultValue = "0") int columnIndex,
                                              @RequestParam(value = "order[0][dir]", defaultValue = "asc") String sortDir) {
        Page<Author> pageAuthors;

        String[] columnNames = {"id", "name", "birthDate"};
        Pageable pageable = getPageable(start, length, columnNames, columnIndex, sortDir);

        if (searchValue != null && !searchValue.isEmpty()) {
            pageAuthors = authorService.searchAuthors(searchValue, pageable);
        } else {
            pageAuthors = authorService.findPaginated(pageable);
        }

        return getResponse(
                draw,
                pageAuthors.getTotalElements(),
                pageAuthors.getTotalElements(),
                pageAuthors.getContent()
        );
    }

    private Pageable getPageable(int start, int length, String[] columnNames, int columnIndex, String sortDir) {
        String sortColumn = columnNames[columnIndex];
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortColumn).ascending() : Sort.by(sortColumn).descending();

        int page = start / length;
        return PageRequest.of(page, length, sort);
    }

    private Map<String, Object> getResponse(int draw, long recordsTotal, long recordsFiltered, List<? extends BaseEntity> data) {
        Map<String, Object> response = new HashMap<>();
        response.put("draw", draw);
        response.put("recordsTotal", recordsTotal);
        response.put("recordsFiltered", recordsFiltered);
        response.put("data", data);
        return response;
    }
}