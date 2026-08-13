package com.ecommerce.ecommerce_backend.controller;

import com.ecommerce.ecommerce_backend.dto.ProductRequest;
import com.ecommerce.ecommerce_backend.entity.Product;
import com.ecommerce.ecommerce_backend.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    public ProductController(ProductService productService){
        this.productService=productService;
    }
    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductRequest request){
        Product createdProduct=productService.createProduct(request);
        return new ResponseEntity<>(
                createdProduct,
                HttpStatus.CREATED
        );
    }
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {

        return ResponseEntity.ok(
                productService.getAllProducts()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                productService.getProductById(id)
        );
    }
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id,@RequestBody Product product){
        return ResponseEntity.ok(productService.updateProduct(id, product));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {

        productService.deleteProduct(id);

        return ResponseEntity.noContent().build();
    }
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(
            @RequestParam String name) {

        return ResponseEntity.ok(
                productService.searchProducts(name)
        );
    }
    @GetMapping("/page")
    public ResponseEntity<Page<Product>> getProducts(
            Pageable pageable) {

        return ResponseEntity.ok(
                productService.getProducts(pageable)
        );
    }

}
