package com.ecommerce.ecommerce_backend.controller;

import com.ecommerce.ecommerce_backend.dto.ProductRequest;
import com.ecommerce.ecommerce_backend.entity.Product;
import com.ecommerce.ecommerce_backend.exception.InvalidPaginationException;
import com.ecommerce.ecommerce_backend.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id",
            "name",
            "price",
            "quantity",
            "category",
            "createdAt"
    );
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
    public ResponseEntity<Page<Product>> getProducts(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @PageableDefault(
                    size = 10,
                    sort = "id"
            ) Pageable pageable){
        if (pageable.getPageSize() > 50) {
            throw new InvalidPaginationException(
                    "Page size cannot be greater than 50"
            );
        }

        if (page < 0){
            throw new InvalidPaginationException(
                    "Page number cannot be negative"
            );
        }
        pageable.getSort().forEach(order -> {

            if (!ALLOWED_SORT_FIELDS.contains(order.getProperty())) {

                throw new InvalidPaginationException(
                        "Invalid sort field: " + order.getProperty()
                );
            }
        });

        return ResponseEntity.ok(
                productService.getProducts(name, pageable)
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



}
