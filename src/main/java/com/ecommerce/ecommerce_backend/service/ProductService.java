package com.ecommerce.ecommerce_backend.service;

import com.ecommerce.ecommerce_backend.dto.ProductRequest;
import com.ecommerce.ecommerce_backend.entity.Product;
import com.ecommerce.ecommerce_backend.exception.ProductNotFoundException;
import com.ecommerce.ecommerce_backend.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    public ProductService(ProductRepository productRepository){
        this.productRepository=productRepository;
    }
    public Product createProduct(ProductRequest request) {

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .category(request.getCategory())
                .imageUrl(request.getImageUrl())
                .build();

        return productRepository.save(product);
    }
    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }
    public Product getProductById(Long id){
        return productRepository.findById(id).orElseThrow(() ->
                new ProductNotFoundException(
                        "Product not found with id: " + id
                )
        );
    }
    public Product updateProduct(Long id,Product updatedProduct){
        Product existingProduct=productRepository.findById(id).orElseThrow(() ->
                new ProductNotFoundException(
                        "Product not found with id: " + id
                )
        );
        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setQuantity(updatedProduct.getQuantity());
        existingProduct.setCategory(updatedProduct.getCategory());
        existingProduct.setImageUrl(updatedProduct.getImageUrl());
        return productRepository.save(existingProduct);
}
    public void deleteProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with id: " + id
                        )
                );

        productRepository.delete(product);
    }
    public List<Product> searchProducts(String name) {

        return productRepository
                .findByNameContainingIgnoreCase(name);
    }
    public Page<Product> getProducts(Pageable pageable) {

        return productRepository.findAll(pageable);
    }
}
