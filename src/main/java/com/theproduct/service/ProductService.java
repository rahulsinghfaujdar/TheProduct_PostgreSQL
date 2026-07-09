package com.theproduct.service;

import com.theproduct.model.Product;
import com.theproduct.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import com.theproduct.model.User;
import com.theproduct.repository.UserRepo;

@Service
public class ProductService {

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private UserRepo userRepo;

    // Get all products
    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    // Get products filtered by category
    public List<Product> getProductsByCategory(String category) {
        return productRepo.findByCategory(category);
    }

    // Get product by ID
    public Optional<Product> getProductById(Integer id) {
        return productRepo.findById(id);
    }

    // Create a new product
    public Product createProduct(Product product) {
        return productRepo.save(product);
    }

    // Update an existing product
    public Product updateProduct(Integer id, Product productDetails) {
        Optional<Product> existingProduct = productRepo.findById(id);
        if (existingProduct.isPresent()) {
            Product product = existingProduct.get();
            if (productDetails.getName() != null) {
                product.setName(productDetails.getName());
            }
            if (productDetails.getDescription() != null) {
                product.setDescription(productDetails.getDescription());
            }
            if (productDetails.getBrand() != null) {
                product.setBrand(productDetails.getBrand());
            }
            if (productDetails.getPrice() != null) {
                product.setPrice(productDetails.getPrice());
            }
            if (productDetails.getCategory() != null) {
                product.setCategory(productDetails.getCategory());
            }
            product.setAvailable(productDetails.isAvailable());
            product.setQuantity(productDetails.getQuantity());
            if (productDetails.getReleaseDate() != null) {
                product.setReleaseDate(productDetails.getReleaseDate());
            }
            if (productDetails.getImageName() != null) {
                product.setImageName(productDetails.getImageName());
            }
            if (productDetails.getImageType() != null) {
                product.setImageType(productDetails.getImageType());
            }
            if (productDetails.getImageData() != null) {
                product.setImageData(productDetails.getImageData());
            }
            return productRepo.save(product);
        }
        return null;
    }

    // Delete a product
    public boolean deleteProduct(Integer id) {
        if (productRepo.existsById(id)) {
            productRepo.deleteById(id);
            return true;
        }
        return false;
    }

    // Search products by name or description
    public List<Product> searchProducts(String searchTerm) {
        return productRepo.searchProducts(searchTerm);
    }

    // Favorites
    public boolean addFavorite(Integer userId, Integer productId) {
        Optional<User> uopt = userRepo.findById(userId);
        Optional<Product> popt = productRepo.findById(productId);
        if (uopt.isPresent() && popt.isPresent()) {
            User user = uopt.get();
            Product product = popt.get();
            user.getFavorites().add(product);
            userRepo.save(user);
            return true;
        }
        return false;
    }

    public boolean removeFavorite(Integer userId, Integer productId) {
        Optional<User> uopt = userRepo.findById(userId);
        if (uopt.isPresent()) {
            User user = uopt.get();
            boolean removed = user.getFavorites().removeIf(p -> p.getId().equals(productId));
            if (removed) {
                userRepo.save(user);
            }
            return removed;
        }
        return false;
    }

    public boolean isFavorite(Integer userId, Integer productId) {
        Optional<User> uopt = userRepo.findById(userId);
        if (uopt.isPresent()) {
            User user = uopt.get();
            return user.getFavorites().stream().anyMatch(p -> p.getId().equals(productId));
        }
        return false;
    }

    public List<Product> getFavoritesForUser(Integer userId) {
        Optional<User> uopt = userRepo.findById(userId);
        if (uopt.isPresent()) {
            User user = uopt.get();
            return new java.util.ArrayList<>(user.getFavorites());
        }
        return java.util.Collections.emptyList();
    }
}
