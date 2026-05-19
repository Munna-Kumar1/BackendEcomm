package com.prasadfencing.backendecom.category.controller;

import com.prasadfencing.backendecom.category.dto.CategoryResponse;
import com.prasadfencing.backendecom.category.dto.CreateCategoryRequest;
import com.prasadfencing.backendecom.category.entity.Category;
import com.prasadfencing.backendecom.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class CategoryController {

    private final CategoryService categoryService;

    // CREATE
    @PostMapping
    public CategoryResponse create(@RequestBody CreateCategoryRequest request) {
        return categoryService.createCategory(request);
    }

    // GET ALL
    @GetMapping
    public List<CategoryResponse> getAll() {
        return categoryService.getAllCategories();
    }

    // GET BY ID
    @GetMapping("/{id}")
    public CategoryResponse getById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }

    // UPDATE
    @PutMapping("/{id}")
    public CategoryResponse update(@PathVariable Long id,
                                   @RequestBody CreateCategoryRequest request) {
        return categoryService.updateCategory(id, request);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        return categoryService.deleteCategory(id);
    }
}