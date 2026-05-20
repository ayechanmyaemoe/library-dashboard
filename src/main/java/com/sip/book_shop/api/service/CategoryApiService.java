package com.sip.book_shop.api.service;

import com.sip.book_shop.api.request.CategoryRequest;
import com.sip.book_shop.api.response.PageCategoryResponse;
import com.sip.book_shop.exception.AlreadyExistsException;
import com.sip.book_shop.exception.NotAllowedException;
import com.sip.book_shop.exception.NotFoundException;
import com.sip.book_shop.mapper.CategoryMapper;
import com.sip.book_shop.model.Book;
import com.sip.book_shop.model.Category;
import com.sip.book_shop.repository.BookRepository;
import com.sip.book_shop.repository.CategoryRepository;
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
public class CategoryApiService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public PageCategoryResponse getAll(int page, int size, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by("id").ascending() : Sort.by("id").descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        List<Category> responseCategories = new ArrayList<>();
        Page<Category> categories = categoryRepository.findAll(pageable);
        for(Category category: categories) {
            responseCategories.add(category);
        }

        List<Category> allCategories = categoryRepository.findAll();
        return PageCategoryResponse.builder()
                .page(page)
                .totalPage(categories.getTotalPages())
                .totalDataCount(allCategories.size())
                .categories(responseCategories)
                .build();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void addNew(CategoryRequest request) {
        Category category = categoryMapper.toEntity(request);
        checkExistCategory(request.getName());
        categoryRepository.save(category);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void update(CategoryRequest request, int id) {
        Category existingCategory = getExistingCategory(id);
        if(!Objects.equals(request.getName(), existingCategory.getName())) {
            checkExistCategory(request.getName());
        }
        Category category = categoryMapper.toEntity(request);
        category.setId(existingCategory.getId());
        categoryRepository.save(category);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void delete(int id) {
        Category existingCategory = getExistingCategory(id);
        List<Book> existingBooks = bookRepository.findByCategoryId(id);
        if(!existingBooks.isEmpty()) {
            throw new NotAllowedException("There are still books with this category. Please delete them first!");
        }
        categoryRepository.deleteById(existingCategory.getId());
    }

    private void checkExistCategory(String name) {
        var category = categoryRepository.findByName(name);
        if(category.isPresent()) {
            throw new AlreadyExistsException("Category already existed!");
        }
    }

    private Category getExistingCategory(int id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("There is no category with such id."));
    }
}
