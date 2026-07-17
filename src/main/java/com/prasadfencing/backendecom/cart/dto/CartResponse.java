package com.prasadfencing.backendecom.cart.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CartResponse {
    private Long cartItemId;
    private Long productId;      // ✅ ADD THIS (VERY IMPORTANT)
    private String productName;
    private Double price;
    private Integer quantity;
    private Double total;
    private String imageUrl;     // ✅ ADD IMAGE
}