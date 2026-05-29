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
import org.springframework.data.jpa.domain.Specification;
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

    public List<AuthorDTO> findAll(AuthorQueryCriteria criteria) {
        Specification<Author> specification = (root, cq, cb) -> QueryHelper.getPredicate(root, criteria, cq, cb);
        Page<Author> pageAuthors = authorRepository.findAll(specification, criteria.getPageable());

        List<AuthorDTO> responseAuthors = new ArrayList<>();
        for(Author author: pageAuthors) {
            responseAuthors.add(authorMapper.toDto(author));
        }

        return responseAuthors;
    }

    public long count() {
        return authorRepository.count();
    }

    public AuthorDTO findById(int id) {
        return authorMapper.toDto(getExistingAuthor(id));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void addNew(AuthorDTO request) {
        checkExistAuthor(request.getName());
        Author author = authorMapper.toEntity(request);
        authorRepository.save(author);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void update(AuthorDTO request, int id) {
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
}
