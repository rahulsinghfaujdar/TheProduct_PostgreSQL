package com.theproduct.repository;

import com.theproduct.model.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCategoryRepo extends JpaRepository<ProductCategory, Integer> {

    // Find category by name
    Optional<ProductCategory> findByName(String name);

    // Find all active categories
    @Query("SELECT pc FROM ProductCategory pc WHERE pc.isActive = true ORDER BY pc.name")
    List<ProductCategory> findAllActiveCategories();

    // Check if category name already exists
    boolean existsByName(String name);
}

