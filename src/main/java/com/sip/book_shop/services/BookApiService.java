package com.sip.book_shop.services;

import com.sip.book_shop.common.query.QueryHelper;
import com.sip.book_shop.dto.BookDTO;
import com.sip.book_shop.dto.mapper.BookMapper;
import com.sip.book_shop.entities.queryCriteria.BookQueryCriteria;
import com.sip.book_shop.vo.BookInfoRequest;
import com.sip.book_shop.common.exceptions.AlreadyExistsException;
import com.sip.book_shop.common.exceptions.NotFoundException;
import com.sip.book_shop.entities.Author;
import com.sip.book_shop.entities.Book;
import com.sip.book_shop.entities.Category;
import com.sip.book_shop.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
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

    public List<BookDTO> getAllResult(BookQueryCriteria criteria) {
        Specification<Book> specification = (root, cq, cb) -> QueryHelper.getPredicate(root, criteria, cq, cb);
        Page<Book> pageBooks = bookRepository.findAll(specification, criteria.getPageable());

        List<BookDTO> responseBooks = new ArrayList<>();
        for(Book book: pageBooks) {
            responseBooks.add(bookMapper.toDto(book));
        }

        return responseBooks;
    }

    public long count() {
        return bookRepository.count();
    }

    public BookDTO findById(int id) {
        return bookMapper.toDto(getExistingBook(id));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void addNew(BookInfoRequest request) {
        Book book = bookMapper.toEntity(request);
        checkExistBook(book.getTitle(), book.getAuthor(), book.getCategory(), book.publishedYear);
        bookRepository.save(book);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void update(BookInfoRequest request, int id) {
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
}
