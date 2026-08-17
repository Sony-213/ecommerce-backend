package com.ecommerce.ecommerce_backend.dto;


import com.ecommerce.ecommerce_backend.entity.OrderStatus;
import lombok.Data;

@Data
public class UpdateOrderStatusRequest {

    private OrderStatus status;
}
