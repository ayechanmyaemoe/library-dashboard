package com.sip.book_shop.services;

import com.sip.book_shop.common.query.QueryHelper;
import com.sip.book_shop.dto.AuthorDTO;
import com.sip.book_shop.dto.mapper.AuthorMapper;
import com.sip.book_shop.entities.queryCriteria.AuthorQueryCriteria;
import com.sip.book_shop.common.exceptions.AlreadyExistsException;
import com.sip.book_shop.common.exceptions.NotAllowedException;
import com.sip.book_shop.common.exceptions.NotFoundException;
import com.sip.book_shop.entities.Author;
import com.sip.book_shop.entities.Book;
import com.sip.book_shop.repositories.AuthorRepository;
import com.sip.book_shop.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;

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

    public Page<AuthorDTO> findAll(AuthorQueryCriteria criteria) {
        Specification<Author> specification = (root, cq, cb) -> QueryHelper.getPredicate(root, criteria, cq, cb);
        Page<Author> pageAuthors = authorRepository.findAll(specification, criteria.getPageable());

        List<AuthorDTO> responseAuthors = pageAuthors.stream().map(authorMapper::toDto)
                .toList();

        return new PageImpl<>(responseAuthors, pageAuthors.getPageable(), pageAuthors.getTotalElements());
    }

    public List<AuthorDTO> getAll() {
        List<AuthorDTO> authorDTOs = new ArrayList<>();
        List<Author> authors = authorRepository.findAll();
        for (Author author : authors) {
            authorDTOs.add(authorMapper.toDto(author));
        }
        return authorDTOs;
    }

    public long count() {
        return authorRepository.count();
    }

    public AuthorDTO findById(int id) {
        return authorMapper.toDto(getExistingAuthor(id));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void addNew(AuthorDTO request) throws BindException {
        checkExistAuthor(request);
        Author author = authorMapper.toEntity(request);
        authorRepository.save(author);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void update(AuthorDTO request, int id) throws BindException {
        Author existingAuthor = getExistingAuthor(id);
        if (existingAuthor.getName().compareToIgnoreCase(request.getName()) != 0) {
            checkExistAuthor(request);
        }
        Author author = authorMapper.toEntity(request);
        author.setId(existingAuthor.getId());
        authorRepository.save(author);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void delete(int id) {
        Author existingAuthor = getExistingAuthor(id);
        List<Book> existingBooks = bookRepository.findByAuthorId(id);
        if (!existingBooks.isEmpty()) {
            throw new NotAllowedException("There are still books with this author. Please delete them first!");
        }
        authorRepository.deleteById(existingAuthor.getId());
    }

    private void checkExistAuthor(AuthorDTO request) throws BindException {
        var author = authorRepository.findByName(request.getName());
        if (author.isPresent()) {
            BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(request, "authorDTO");
            bindingResult.rejectValue("name", "duplicate", "Author already existed!");
            throw new BindException(bindingResult);
        }
    }

    private Author getExistingAuthor(int id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("There is no author with such id."));
    }
}
