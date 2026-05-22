package com.sip.book_shop.api.service;

import com.sip.book_shop.api.request.AuthorRequest;
import com.sip.book_shop.api.response.PageAuthorResponse;
import com.sip.book_shop.api.response.PageCategoryResponse;
import com.sip.book_shop.api.response.SingleBookResponse;
import com.sip.book_shop.exception.AlreadyExistsException;
import com.sip.book_shop.exception.NotAllowedException;
import com.sip.book_shop.exception.NotFoundException;
import com.sip.book_shop.mapper.AuthorMapper;
import com.sip.book_shop.model.Author;
import com.sip.book_shop.model.Book;
import com.sip.book_shop.model.Category;
import com.sip.book_shop.repository.AuthorRepository;
import com.sip.book_shop.repository.BookRepository;
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
public class AuthorApiService {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorMapper authorMapper;

    public PageAuthorResponse getAll(int page, int size, String sortDir, String searchValue) {
        Page<Author> authors;
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by("id").ascending() : Sort.by("id").descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        if(searchValue != null && !searchValue.isEmpty()) {
            authors = searchAuthors(searchValue, pageable);
        } else {
            authors = authorRepository.findAll(pageable);
        }

        List<Author> responseAuthors = new ArrayList<>();
        for(Author author: authors) {
            responseAuthors.add(author);
        }

        return PageAuthorResponse.builder()
                .page(page)
                .totalPage(authors.getTotalPages())
                .totalDataCount(authorRepository.findAll().size())
                .authors(responseAuthors)
                .build();
    }

    public Author getById(int id) {
        return getExistingAuthor(id);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void addNew(AuthorRequest request) {
        checkExistAuthor(request.getName());
        Author author = authorMapper.toEntity(request);
        authorRepository.save(author);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void update(AuthorRequest request, int id) {
        Author existingAuthor = getExistingAuthor(id);
        if(!Objects.equals(existingAuthor.getName(), request.getName())) {
            checkExistAuthor(request.getName());
        }
        Author author = authorMapper.toEntity(request);
        author.setId(existingAuthor.getId());
        authorRepository.save(author);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void delete(int id) {
        Author existingAuthor = getExistingAuthor(id);
        List<Book> existingBooks = bookRepository.findByAuthorId(id);
        if(!existingBooks.isEmpty()) {
            throw new NotAllowedException("There are still books with this author. Please delete them first!");
        }
        authorRepository.deleteById(existingAuthor.getId());
    }

    private void checkExistAuthor(String name) {
        var author = authorRepository.findByName(name);
        if(author.isPresent()) {
            throw new AlreadyExistsException("Author already existed!");
        }
    }

    private Author getExistingAuthor(int id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("There is no author with such id."));
    }

    public Page<Author> searchAuthors(String searchValue, Pageable pageable) {
        return authorRepository.searchByKeyword(searchValue, pageable);
    }
}
