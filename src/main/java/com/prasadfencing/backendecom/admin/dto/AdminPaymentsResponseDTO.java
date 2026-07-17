package com.prasadfencing.backendecom.admin.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class AdminPaymentsResponseDTO {
    private Long id;
    private Double amount;
    private String status;
    private String paymentMethod;
    private LocalDateTime createdAt;
    private Long orderId;
    private String customerName;
    private String customerEmail;
}