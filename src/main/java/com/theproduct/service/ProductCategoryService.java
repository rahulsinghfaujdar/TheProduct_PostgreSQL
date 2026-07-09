package com.theproduct.service;

import com.theproduct.model.ProductCategory;
import com.theproduct.repository.ProductCategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductCategoryService {

    @Autowired
    private ProductCategoryRepo productCategoryRepo;

    // Get all categories
    public List<ProductCategory> getAllCategories() {
        return productCategoryRepo.findAll();
    }

    // Get all active categories
    public List<ProductCategory> getAllActiveCategories() {
        return productCategoryRepo.findAllActiveCategories();
    }

    // Get category by ID
    public Optional<ProductCategory> getCategoryById(Integer id) {
        return productCategoryRepo.findById(id);
    }

    // Get category by name
    public Optional<ProductCategory> getCategoryByName(String name) {
        return productCategoryRepo.findByName(name);
    }

    // Create a new category
    public ProductCategory createCategory(ProductCategory category) {
        if (productCategoryRepo.existsByName(category.getName())) {
            throw new IllegalArgumentException("Category with name '" + category.getName() + "' already exists");
        }
        return productCategoryRepo.save(category);
    }

    // Update an existing category
    public ProductCategory updateCategory(Integer id, ProductCategory categoryDetails) {
        Optional<ProductCategory> existingCategory = productCategoryRepo.findById(id);
        if (existingCategory.isPresent()) {
            ProductCategory category = existingCategory.get();

            // Check if new name already exists (excluding current category)
            if (categoryDetails.getName() != null &&
                !categoryDetails.getName().equals(category.getName()) &&
                productCategoryRepo.existsByName(categoryDetails.getName())) {
                throw new IllegalArgumentException("Category with name '" + categoryDetails.getName() + "' already exists");
            }

            if (categoryDetails.getName() != null) {
                category.setName(categoryDetails.getName());
            }
            if (categoryDetails.getDescription() != null) {
                category.setDescription(categoryDetails.getDescription());
            }
            category.setActive(categoryDetails.isActive());

            return productCategoryRepo.save(category);
        }
        return null;
    }

    // Delete a category
    public boolean deleteCategory(Integer id) {
        if (productCategoryRepo.existsById(id)) {
            productCategoryRepo.deleteById(id);
            return true;
        }
        return false;
    }

    // Check if category name exists
    public boolean categoryNameExists(String name) {
        return productCategoryRepo.existsByName(name);
    }
}

