package com.ecommerce.ecommerce_backend.controller;

import com.ecommerce.ecommerce_backend.dto.OrderResponse;
import com.ecommerce.ecommerce_backend.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder() {

        return ResponseEntity.ok(
                orderService.placeOrder()
        );
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getMyOrders() {

        return ResponseEntity.ok(
                orderService.getMyOrders()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                orderService.getOrder(id)
        );
    }
}