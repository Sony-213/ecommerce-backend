package com.ecommerce.ecommerce_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data//allows getterand setter methods
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name="products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer quantity;

    private String category;

    private String imageUrl;
    private LocalDateTime createdAt;
    @PrePersist  //gives correct time and data
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }


}
