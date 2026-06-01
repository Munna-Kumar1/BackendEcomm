package com.prasadfencing.backendecom.cart.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CartResponse {
    private Long cartItemId;
    private String productName;
    private Double price;
    private Integer quantity;
    private Double total;
}
