package com.theproduct.controller;

import com.theproduct.model.Product;
import com.theproduct.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@CrossOrigin
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // Get all products with optional category filter
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(@RequestParam(required = false) String category) {
        List<Product> products;
        if (category != null && !category.isEmpty()) {
            products = productService.getProductsByCategory(category);
        } else {
            products = productService.getAllProducts();
        }
        return ResponseEntity.ok(products);
    }

    // Get product by ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Integer id) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent()) {
            return ResponseEntity.ok(product.get());
        }
        return ResponseEntity.notFound().build();
    }

    // Get product image by product ID
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getProductImage(@PathVariable Integer id) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent() && product.get().getImageData() != null) {
            Product p = product.get();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(p.getImageType() != null ? p.getImageType() : "image/jpeg"));
            headers.setContentLength(p.getImageData().length);
            return new ResponseEntity<>(p.getImageData(), headers, HttpStatus.OK);

            //Map<String, Object> resp = new java.util.HashMap<>();
            //resp.put("imageData",p.getImageData());
            //resp.put("imageType",p.getImageType());
            //resp.put("imageName",p.getImageName());
            //return new ResponseEntity<>(resp, headers, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

    // Create a new product
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> createProduct(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer quantity,
            @RequestParam(required = false) String price,
            @RequestParam(required = false) String releaseDate,
            @RequestParam(required = false) Boolean available,
            @RequestParam(required = false) MultipartFile imageFile) {
        Map<String, Object> resp = new java.util.HashMap<>();
        try {
            Product product = new Product();
            product.setName(name);
            product.setDescription(description);
            product.setBrand(brand);
            product.setCategory(category);
            product.setQuantity(quantity != null ? quantity : 0);
            product.setAvailable(available != null ? available : false);

            // Parse price
            if (price != null && !price.isEmpty()) {
                product.setPrice(new java.math.BigDecimal(price));
            }

            // Parse release date (format: yyyy-MM-dd)
            if (releaseDate != null && !releaseDate.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                product.setReleaseDate(sdf.parse(releaseDate));
            }

            // Handle image file
            if (imageFile != null && !imageFile.isEmpty()) {
                product.setImageName(imageFile.getOriginalFilename());
                product.setImageType(imageFile.getContentType());
                product.setImageData(imageFile.getBytes());
            }

            System.out.println("Add Product : " + product);
            Product createdProduct = productService.createProduct(product);
            resp.put("success", true);
            resp.put("product", createdProduct);
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (IOException ex) {
            resp.put("success", false);
            resp.put("error", "File upload error: " + ex.getMessage());
            return ResponseEntity.badRequest().body(resp);
        } catch (Exception ex) {
            resp.put("success", false);
            resp.put("error", "Error creating product: " + ex.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    // Create a new product (JSON endpoint for backwards compatibility)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> createProductJson(@RequestBody Product product) {
        Map<String, Object> resp = new java.util.HashMap<>();
        try {
            System.out.println("Add Product (JSON) : " + product);
            Product createdProduct = productService.createProduct(product);
            resp.put("success", true);
            resp.put("product", createdProduct);
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (Exception ex) {
            resp.put("success", false);
            resp.put("error", "Error creating product: " + ex.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    // Update an existing product (multipart/form-data endpoint)
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> updateProduct(
            @PathVariable Integer id,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam String brand,
            @RequestParam String category,
            @RequestParam Integer quantity,
            @RequestParam String price,
            @RequestParam String releaseDate,
            @RequestParam Boolean available,
            @RequestParam(required = false) MultipartFile imageFile) {
        Map<String, Object> resp = new java.util.HashMap<>();
        try {
            // Validate required fields
            if (name == null || name.trim().isEmpty()) {
                resp.put("success", false);
                resp.put("error", "name is required");
                return ResponseEntity.badRequest().body(resp);
            }
            if (description == null || description.trim().isEmpty()) {
                resp.put("success", false);
                resp.put("error", "description is required");
                return ResponseEntity.badRequest().body(resp);
            }
            if (brand == null || brand.trim().isEmpty()) {
                resp.put("success", false);
                resp.put("error", "brand is required");
                return ResponseEntity.badRequest().body(resp);
            }
            if (category == null || category.trim().isEmpty()) {
                resp.put("success", false);
                resp.put("error", "category is required");
                return ResponseEntity.badRequest().body(resp);
            }
            if (quantity == null) {
                resp.put("success", false);
                resp.put("error", "quantity is required");
                return ResponseEntity.badRequest().body(resp);
            }
            if (price == null || price.trim().isEmpty()) {
                resp.put("success", false);
                resp.put("error", "price is required");
                return ResponseEntity.badRequest().body(resp);
            }
            if (releaseDate == null || releaseDate.trim().isEmpty()) {
                resp.put("success", false);
                resp.put("error", "releaseDate is required");
                return ResponseEntity.badRequest().body(resp);
            }
            if (available == null) {
                resp.put("success", false);
                resp.put("error", "available is required");
                return ResponseEntity.badRequest().body(resp);
            }

            Product productDetails = new Product();
            productDetails.setName(name);
            productDetails.setDescription(description);
            productDetails.setBrand(brand);
            productDetails.setCategory(category);
            productDetails.setQuantity(quantity);
            productDetails.setAvailable(available);

            // Parse price
            productDetails.setPrice(new java.math.BigDecimal(price));

            // Parse release date (format: yyyy-MM-dd)
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            productDetails.setReleaseDate(sdf.parse(releaseDate));

            // Handle image file (optional)
            if (imageFile != null && !imageFile.isEmpty()) {
                productDetails.setImageName(imageFile.getOriginalFilename());
                productDetails.setImageType(imageFile.getContentType());
                productDetails.setImageData(imageFile.getBytes());
            }

            System.out.println("Update Product : " + productDetails);
            Product updatedProduct = productService.updateProduct(id, productDetails);
            if (updatedProduct != null) {
                resp.put("success", true);
                resp.put("product", updatedProduct);
                return ResponseEntity.ok(resp);
            } else {
                resp.put("success", false);
                resp.put("error", "Product not found");
                return ResponseEntity.notFound().build();
            }
        } catch (IOException ex) {
            resp.put("success", false);
            resp.put("error", "File upload error: " + ex.getMessage());
            return ResponseEntity.badRequest().body(resp);
        } catch (Exception ex) {
            resp.put("success", false);
            resp.put("error", "Error updating product: " + ex.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    // Update product (JSON endpoint for backwards compatibility)
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> updateProductJson(@PathVariable Integer id, @RequestBody Product productDetails) {
        Map<String, Object> resp = new java.util.HashMap<>();
        try {
            System.out.println("Update Product (JSON) : " + productDetails);
            Product updatedProduct = productService.updateProduct(id, productDetails);
            if (updatedProduct != null) {
                resp.put("success", true);
                resp.put("product", updatedProduct);
                return ResponseEntity.ok(resp);
            } else {
                resp.put("success", false);
                resp.put("error", "Product not found");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex) {
            resp.put("success", false);
            resp.put("error", "Error updating product: " + ex.getMessage());
            return ResponseEntity.badRequest().body(resp);
        }
    }

    // Delete a product
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        if (productService.deleteProduct(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Search products by name or description
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String query) {
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<Product> results = productService.searchProducts(query);
        return ResponseEntity.ok(results);
    }

    // Add product to user's favorites
    @PostMapping("/{id}/favorite")
    public ResponseEntity<Void> addFavorite(@PathVariable Integer id, @RequestParam Integer userId) {
        boolean ok = productService.addFavorite(userId, id);
        if (ok) return ResponseEntity.status(HttpStatus.CREATED).build();
        return ResponseEntity.notFound().build();
    }

    // Remove product from user's favorites
    @DeleteMapping("/{id}/favorite")
    public ResponseEntity<Void> removeFavorite(@PathVariable Integer id, @RequestParam Integer userId) {
        boolean ok = productService.removeFavorite(userId, id);
        if (ok) return ResponseEntity.noContent().build();
        return ResponseEntity.notFound().build();
    }

    // Check if product is favorite for user
    @GetMapping("/{id}/favorite")
    public ResponseEntity<Map<String, Object>> isFavorite(@PathVariable Integer id, @RequestParam Integer userId) {
        boolean fav = productService.isFavorite(userId, id);
        Map<String, Object> resp = new java.util.HashMap<>();
        resp.put("productId", id);
        resp.put("userId", userId);
        resp.put("isFavorite", fav);
        return ResponseEntity.ok(resp);
    }

    // Get favorites list for a user
    @GetMapping("/favorites")
    public ResponseEntity<List<Product>> getFavorites(@RequestParam Integer userId) {
        List<Product> favs = productService.getFavoritesForUser(userId);
        return ResponseEntity.ok(favs);
    }
}