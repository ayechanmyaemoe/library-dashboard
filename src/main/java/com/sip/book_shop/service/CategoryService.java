package com.sip.book_shop.service;

import com.sip.book_shop.helper.MessageHelper;
import com.sip.book_shop.model.Author;
import com.sip.book_shop.model.Book;
import com.sip.book_shop.model.Category;
import com.sip.book_shop.repository.BookRepository;
import com.sip.book_shop.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BookRepository bookRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Page<Category> findPaginated(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    public Page<Category> searchCategories(String searchValue, Pageable pageable) {
        return categoryRepository.searchByKeyword(searchValue, pageable);
    }

    public int countAllCategories() {
        return categoryRepository.findAll().size();
    }

    public Category getCategoryById(int id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(MessageHelper.getMessage("category.error.notFound")));
    }

    public Category findByName(String name) {
        return categoryRepository.findByName(name);
    }

    public void saveCategory(Category category) {
        categoryRepository.save(category);
    }

    public void deleteCategoryById(int id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(MessageHelper.getMessage("category.error.notFound")));

        List<Book> books = bookRepository.findByCategoryId(category.getId());

        if(!books.isEmpty()) {
            throw new IllegalStateException(MessageHelper.getMessage("category.error.denyDeletion"));
        }

        categoryRepository.deleteById(category.id);
    }
}
