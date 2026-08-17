package com.ecommerce.ecommerce_backend.service;

import com.ecommerce.ecommerce_backend.dto.OrderItemResponse;
import com.ecommerce.ecommerce_backend.dto.OrderResponse;
import com.ecommerce.ecommerce_backend.entity.*;
import com.ecommerce.ecommerce_backend.repository.CartRepository;
import com.ecommerce.ecommerce_backend.repository.OrderRepository;
import com.ecommerce.ecommerce_backend.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    public OrderService(
            OrderRepository orderRepository,
            CartRepository cartRepository,
            UserRepository userRepository) {

        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }

    @Transactional
    public OrderResponse placeOrder() {

        User user = getCurrentUser();

        Cart cart = cartRepository
                .findByUserId(user.getId())
                .orElseThrow(() ->
                        new RuntimeException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException(
                    "Cannot place order with empty cart"
            );
        }

        Order order = new Order();

        order.setUser(user);
        order.setStatus(OrderStatus.PLACED);
        order.setCreatedAt(
                java.time.LocalDateTime.now()
        );

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {

            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());

            BigDecimal price =
                    cartItem.getProduct().getPrice();

            orderItem.setPrice(price);

            BigDecimal itemTotal =
                    price.multiply(
                            BigDecimal.valueOf(
                                    cartItem.getQuantity()
                            )
                    );

            total = total.add(itemTotal);

            order.getItems().add(orderItem);
        }

        order.setTotalAmount(total);

        Order savedOrder =
                orderRepository.save(order);

        cart.getItems().clear();

        cartRepository.save(cart);

        return convertToOrderResponse(savedOrder);
    }

    public List<OrderResponse> getMyOrders() {

        User user = getCurrentUser();

        List<Order> orders =
                orderRepository
                        .findByUserOrderByCreatedAtDesc(user);

        return orders.stream()
                .map(this::convertToOrderResponse)
                .toList();
    }

    public OrderResponse getOrder(Long orderId) {

        User user = getCurrentUser();

        Order order =
                orderRepository
                        .findByIdAndUser(orderId, user)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order not found"
                                ));

        return convertToOrderResponse(order);
    }

    private OrderResponse convertToOrderResponse(Order order) {

        List<OrderItemResponse> items =
                order.getItems()
                        .stream()
                        .map(item ->
                                OrderItemResponse.builder()
                                        .productId(
                                                item.getProduct().getId()
                                        )
                                        .productName(
                                                item.getProduct().getName()
                                        )
                                        .quantity(
                                                item.getQuantity()
                                        )
                                        .price(
                                                item.getPrice()
                                        )
                                        .build()
                        )
                        .toList();

        return OrderResponse.builder()
                .orderId(order.getId())
                .userId(order.getUser().getId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .items(items)
                .build();
    }
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {

        List<Order> orders =
                orderRepository.findAll();

        return orders.stream()
                .map(this::convertToOrderResponse)
                .toList();
    }
    @Transactional(readOnly = true)
    public OrderResponse getOrderForAdmin(Long orderId) {

        Order order =
                orderRepository.findById(orderId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order not found"
                                ));

        return convertToOrderResponse(order);
    }
    @Transactional
    public OrderResponse updateOrderStatus(
            Long orderId,
            OrderStatus newStatus) {

        Order order =
                orderRepository.findById(orderId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order not found"
                                ));

        OrderStatus currentStatus =
                order.getStatus();

        if (!isValidStatusTransition(
                currentStatus,
                newStatus)) {

            throw new RuntimeException(
                    "Invalid order status transition: "
                            + currentStatus
                            + " -> "
                            + newStatus
            );
        }

        order.setStatus(newStatus);

        Order updatedOrder =
                orderRepository.save(order);

        return convertToOrderResponse(updatedOrder);
    }
    private boolean isValidStatusTransition(
            OrderStatus currentStatus,
            OrderStatus newStatus) {

        // Same status is allowed
        if (currentStatus == newStatus) {
            return true;
        }

        // PLACED → CONFIRMED
        if (currentStatus == OrderStatus.PLACED
                && newStatus == OrderStatus.CONFIRMED) {
            return true;
        }

        // PLACED → CANCELLED
        if (currentStatus == OrderStatus.PLACED
                && newStatus == OrderStatus.CANCELLED) {
            return true;
        }

        // CONFIRMED → SHIPPED
        if (currentStatus == OrderStatus.CONFIRMED
                && newStatus == OrderStatus.SHIPPED) {
            return true;
        }

        // SHIPPED → DELIVERED
        if (currentStatus == OrderStatus.SHIPPED
                && newStatus == OrderStatus.DELIVERED) {
            return true;
        }

        // Everything else is invalid
        return false;
    }
}