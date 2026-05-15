package com.sip.book_shop.service;

import com.sip.book_shop.exception.AlreadyExistsException;
import com.sip.book_shop.helper.MessageHelper;
import com.sip.book_shop.model.Book;
import com.sip.book_shop.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public void saveBook(Book book) {
        Optional<Book> existingBook = bookRepository.findByTitleAndAuthorAndCategoryAndPublishedYear(
                book.getTitle(),
                book.getAuthor(),
                book.getCategory(),
                book.getPublishedYear()
        );
        if(existingBook.isPresent() && book.getId() != existingBook.get().getId()) {
            throw new AlreadyExistsException(MessageHelper.getMessage("book.error.alreadyExists"));
        }
        bookRepository.save(book);
    }

    public int countAllBooks() {
        return bookRepository.findAll().size();
    }

    public Book getBookById(int id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(MessageHelper.getMessage("book.error.notFound")));
    }

    public void deleteBookById(int id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(MessageHelper.getMessage("book.error.notFound")));
        bookRepository.deleteById(book.getId());
    }

    public Page<Book> findPaginated(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    public Page<Book> searchBooks(String searchValue, Pageable pageable) {
        return bookRepository.searchByKeyword(searchValue, pageable);
    }

}
