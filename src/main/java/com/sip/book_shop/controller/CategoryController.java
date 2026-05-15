package com.sip.book_shop.controller;

import com.sip.book_shop.dto.CategoryDto;
import com.sip.book_shop.exception.AlreadyExistsException;
import com.sip.book_shop.helper.MessageHelper;
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
    public String insertOrUpdateCategory(@Valid @ModelAttribute CategoryDto categoryDto,
                                         BindingResult bindingResult,
                                         RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()) {
            return (categoryDto.getId() != null) ? "edit-category-page" : "add-category-page";
        }

        boolean isUpdate = categoryDto.getId() != null;
        try {
            Category category;
            if(isUpdate) {
                category = categoryService.getCategoryById(categoryDto.getId());
                category.setName(categoryDto.getName().trim());
            } else {
                category = categoryMapper.toEntity(categoryDto);
            }
            categoryService.saveCategory(category);
            String messageKey = isUpdate ? "category.success.edit" : "category.success.create";
            redirectAttributes.addFlashAttribute("success", MessageHelper.getMessage(messageKey));
        } catch (AlreadyExistsException e) {
            bindingResult.rejectValue("name", e.getMessage(), "error.alreadyExists");
            return isUpdate ? "edit-category-page" : "add-category-page";
        }
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
