package com.sip.book_shop.api.service;

import com.sip.book_shop.api.request.BookRequest;
import com.sip.book_shop.api.response.BookResponse;
import com.sip.book_shop.api.response.PageBookResponse;
import com.sip.book_shop.api.response.SingleBookResponse;
import com.sip.book_shop.exception.AlreadyExistsException;
import com.sip.book_shop.exception.NotFoundException;
import com.sip.book_shop.mapper.BookMapper;
import com.sip.book_shop.model.Author;
import com.sip.book_shop.model.Book;
import com.sip.book_shop.model.Category;
import com.sip.book_shop.repository.BookRepository;
import com.sip.book_shop.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class BookApiService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookMapper bookMapper;

    public PageBookResponse getAll(int page, int size, String sortDir, String searchValue) {
        Page<Book> books;
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by("id").ascending() : Sort.by("id").descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        if(searchValue != null && !searchValue.isEmpty()) {
            books = searchBooks(searchValue, pageable);
        } else {
            books = bookRepository.findAll(pageable);
        }

        List<BookResponse> responseBooks = new ArrayList<>();
        for(Book book: books) {
            responseBooks.add(bookMapper.toResponse(book));
        }

        return PageBookResponse.builder()
                .page(page)
                .totalPage(books.getTotalPages())
                .totalDataCount(bookRepository.findAll().size())
                .books(responseBooks)
                .build();
    }

    public SingleBookResponse getById(int id) {
        Book book = getExistingBook(id);
        return bookMapper.toSingleResponse(book);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void addNew(BookRequest request) {
        Book book = bookMapper.toEntity(request);
        checkExistBook(book.getTitle(), book.getAuthor(), book.getCategory(), book.publishedYear);
        bookRepository.save(book);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void update(BookRequest request, int id) {
        Book existingBook = getExistingBook(id);
        Book updateBook = bookMapper.toEntity(request);
        if(!Objects.equals(existingBook.getTitle(), updateBook.getTitle())) {
            checkExistBook(updateBook.getTitle(), updateBook.getAuthor(), updateBook.getCategory(), updateBook.publishedYear);
        }
        updateBook.setId(existingBook.getId());
        bookRepository.save(updateBook);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void delete(int id) {
        Book existingBook = getExistingBook(id);
        bookRepository.deleteById(existingBook.getId());
    }

    private void checkExistBook(String title, Author author, Category category, int publishedYear) {
        var book = bookRepository.findByTitleAndAuthorAndCategoryAndPublishedYear(
                title, author, category, publishedYear);
        if(book.isPresent()) {
            throw new AlreadyExistsException("Book already existed!");
        }
    }

    private Book getExistingBook(int id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("There is no book with such id."));
    }

    public Page<Book> searchBooks(String searchValue, Pageable pageable) {
        return bookRepository.searchByKeyword(searchValue, pageable);
    }
}
