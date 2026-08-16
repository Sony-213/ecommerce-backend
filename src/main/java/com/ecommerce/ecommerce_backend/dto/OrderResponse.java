package com.ecommerce.ecommerce_backend.dto;



import com.ecommerce.ecommerce_backend.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {

    private Long orderId;

    private Long userId;

    private BigDecimal totalAmount;

    private OrderStatus status;

    private LocalDateTime createdAt;

    private List<OrderItemResponse> items;
}
