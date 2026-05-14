package com.sip.book_shop.service;

import com.sip.book_shop.model.Book;
import com.sip.book_shop.repository.BookRepository;
import com.sun.jdi.request.DuplicateRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public void saveBook(Book book) {
        boolean exists = bookRepository.existsByTitleAndAuthorAndCategoryAndPublishedYear(
                book.getTitle(),
                book.getAuthor(),
                book.getCategory(),
                book.getPublishedYear()
        );
        if(exists) {
            throw new DuplicateRequestException("A book with these details already exists!");
        }
        bookRepository.save(book);
    }

    public int countAllBooks() {
        return bookRepository.findAll().size();
    }

    public Book getBookById(int id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("There is no book with such id."));
    }

    public void deleteBookById(int id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("There is no book with such id"));
        bookRepository.deleteById(book.getId());
    }

    public Page<Book> findPaginated(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    public Page<Book> searchBooks(String searchValue, Pageable pageable) {
        return bookRepository.searchByKeyword(searchValue, pageable);
    }

}
