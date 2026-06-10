package com.sip.book_shop.services;

import com.sip.book_shop.common.query.QueryHelper;
import com.sip.book_shop.dto.CategoryDTO;
import com.sip.book_shop.dto.mapper.CategoryMapper;
import com.sip.book_shop.entities.queryCriteria.CategoryQueryCriteria;
import com.sip.book_shop.common.exceptions.AlreadyExistsException;
import com.sip.book_shop.common.exceptions.NotAllowedException;
import com.sip.book_shop.common.exceptions.NotFoundException;
import com.sip.book_shop.entities.Book;
import com.sip.book_shop.entities.Category;
import com.sip.book_shop.repositories.BookRepository;
import com.sip.book_shop.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;

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

//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Page<CategoryDTO> getAllResult(CategoryQueryCriteria criteria) {
        Specification<Category> specification = (root, cq, cb) -> QueryHelper.getPredicate(root, criteria, cq, cb);
        Page<Category> pageCategories = categoryRepository.findAll(specification, criteria.getPageable());

        List<CategoryDTO> responseCategories = pageCategories.stream().map(categoryMapper::toDto)
                .toList();

        return new PageImpl<>(responseCategories, pageCategories.getPageable(), pageCategories.getTotalElements());
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<CategoryDTO> getAll() {
        List<CategoryDTO> categoryDTOs = new ArrayList<>();
        List<Category>  categories = categoryRepository.findAll();
        for(Category category: categories) {
            categoryDTOs.add(categoryMapper.toDto(category));
        }
        return categoryDTOs;
    }

    public long count() {
        return categoryRepository.count();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public CategoryDTO findById(int id) {
        return categoryMapper.toDto(getExistingCategory(id));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void addNew(CategoryDTO request) throws BindException {
        Category category = categoryMapper.toEntity(request);
        checkExistCategory(request);
        categoryRepository.save(category);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void update(CategoryDTO request, int id) throws BindException {
        Category existingCategory = getExistingCategory(id);
        if(request.getName().compareToIgnoreCase(existingCategory.getName()) != 0) {
            checkExistCategory(request);
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

    private void checkExistCategory(CategoryDTO request) throws BindException {
        var category = categoryRepository.findByName(request.getName());
        if(category.isPresent()) {
            BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(request, "categoryDTO");
            bindingResult.rejectValue("name", "duplicate", "Category already existed!");
            throw new BindException(bindingResult);
        }
    }

    private Category getExistingCategory(int id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("There is no category with such id."));
    }
}
