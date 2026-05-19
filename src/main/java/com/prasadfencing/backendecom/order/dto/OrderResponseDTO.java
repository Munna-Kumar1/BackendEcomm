package com.prasadfencing.backendecom.order.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class OrderResponseDTO {
    private Long orderId;
    private String status;
    private LocalDateTime orderDate;
    private Double totalAmount;
    private Long addressId;
    private List<OrderItemResponseDTO> items;
}
