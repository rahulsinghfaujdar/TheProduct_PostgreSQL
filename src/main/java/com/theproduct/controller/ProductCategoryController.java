package com.theproduct.controller;

import com.theproduct.model.ProductCategory;
import com.theproduct.repository.ProductCategoryRepo;
import com.theproduct.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/product_categories")
public class ProductCategoryController {

    @Autowired
    private ProductCategoryService productCategoryService;

    // Get all categories
    @GetMapping
    public ResponseEntity<List<ProductCategory>> getAllCategories() {
        List<ProductCategory> categories = productCategoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    // Get all active categories
    @GetMapping("/active")
    public ResponseEntity<List<ProductCategory>> getAllActiveCategories() {
        List<ProductCategory> categories = productCategoryService.getAllActiveCategories();
        return ResponseEntity.ok(categories);
    }

    // Get category by ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductCategory> getCategoryById(@PathVariable Integer id) {
        Optional<ProductCategory> category = productCategoryService.getCategoryById(id);
        if (category.isPresent()) {
            return ResponseEntity.ok(category.get());
        }
        return ResponseEntity.notFound().build();
    }

    // Get category by name
    @GetMapping("/name/{name}")
    public ResponseEntity<ProductCategory> getCategoryByName(@PathVariable String name) {
        Optional<ProductCategory> category = productCategoryService.getCategoryByName(name);
        if (category.isPresent()) {
            return ResponseEntity.ok(category.get());
        }
        return ResponseEntity.notFound().build();
    }

    // Create a new category
    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody ProductCategory category) {
        try {
            ProductCategory createdCategory = productCategoryService.createCategory(category);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Update an existing category
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Integer id, @RequestBody ProductCategory categoryDetails) {
        try {
            ProductCategory updatedCategory = productCategoryService.updateCategory(id, categoryDetails);
            if (updatedCategory != null) {
                return ResponseEntity.ok(updatedCategory);
            }
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Delete a category
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) {
        if (productCategoryService.deleteCategory(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Check if category name exists
    @GetMapping("/exists/{name}")
    public ResponseEntity<Boolean> categoryNameExists(@PathVariable String name) {
        boolean exists = productCategoryService.categoryNameExists(name);
        return ResponseEntity.ok(exists);
    }
}

