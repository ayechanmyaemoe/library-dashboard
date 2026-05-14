package com.sip.book_shop.controller;

import com.sip.book_shop.dto.CategoryDto;
import com.sip.book_shop.mapper.CategoryMapper;
import com.sip.book_shop.model.Category;
import com.sip.book_shop.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryMapper categoryMapper;

    @GetMapping
    public String getAllCategory(Model model) {
        model.addAttribute("module", "categories");
        return "category-list-page";
    }

    @GetMapping("/add")
    public String addCategory(Model model) {
        model.addAttribute("categoryDto", new CategoryDto());
        return "add-category-page";
    }

    @PostMapping("/save")
    public String insertOrUpdateCategory(@Valid @ModelAttribute CategoryDto categoryDto, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            if(categoryDto.getId() != null) {
                return "edit-category-page";
            } else {
                return "add-category-page";
            }
        }

        Category category;
        if(categoryDto.getId() != null) {
            category = categoryService.getCategoryById(categoryDto.getId());
            if(!Objects.equals(category.getName(), categoryDto.getName())) {
                if(categoryService.findByName(categoryDto.getName().trim()) != null) {
                    bindingResult.rejectValue("name", "category.error.alreadyExists", "error.alreadyExists");
                    return "edit-category-page";
                }
            }
            category.setName(categoryDto.getName().trim());
        } else {
            if(categoryService.findByName(categoryDto.getName().trim()) != null) {
                bindingResult.rejectValue("name", "category.error.alreadyExists", "error.alreadyExists");
                return "add-category-page";
            }
            category = categoryMapper.toEntity(categoryDto);
        }
        categoryService.saveCategory(category);
        return "redirect:/categories";
    }

    @GetMapping("/{id}/edit")
    public String editCategory(Model model, @PathVariable int id) {
        Category category = categoryService.getCategoryById(id);
        CategoryDto categoryDto = categoryMapper.toDto(category);
        model.addAttribute("categoryDto", categoryDto);
        return "edit-category-page";
    }

    @DeleteMapping("/{id}")
    public String deleteCategory(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategoryById(id);
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/categories";
    }
}
