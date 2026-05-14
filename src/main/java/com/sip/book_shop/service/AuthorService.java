package com.sip.book_shop.service;

import com.sip.book_shop.model.Author;
import com.sip.book_shop.model.Book;
import com.sip.book_shop.repository.AuthorRepository;
import com.sip.book_shop.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    public void saveAuthor(Author author) {
        authorRepository.save(author);
    }

    public int countAllAuthors() {
        return authorRepository.findAll().size();
    }

    public Author getAuthorById(int id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("There is no author with such id."));
    }

    public void deleteAuthorById(int id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("There is no author with such id"));

        List<Book> books = bookRepository.findByAuthorId(author.getId());

        if(!books.isEmpty()) {
            throw new IllegalStateException("There are still books with this author. You must delete them first!");
        }

        authorRepository.deleteById(author.id);
    }

    public Page<Author> findPaginated(Pageable pageable) {
        return authorRepository.findAll(pageable);
    }

    public Page<Author> searchAuthors(String searchValue, Pageable pageable) {
        return authorRepository.searchByKeyword(searchValue, pageable);
    }

    public Author findByName(String name) {
        return authorRepository.findByName(name);
    }
}
