package com.prasadfencing.backendecom.admin.dto;

import com.prasadfencing.backendecom.order.dto.OrderItemResponseDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class AdminDashboardOverviewDTO {
    // Order Context
    private Long orderId;
    private LocalDateTime orderDate;
    private String orderStatus;
    private Double totalAmount;

    // User Context (Who)
    private String customerName;
    private String customerEmail;

    // Address Context (Where)
    private String shippingName;
    private String shippingStreet;
    private String shippingCity;
    private String shippingState;
    private String shippingPincode;

    // Products Context (What)
    private List<OrderItemResponseDTO> items;
}