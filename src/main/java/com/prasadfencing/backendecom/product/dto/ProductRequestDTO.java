package com.prasadfencing.backendecom.product.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequestDTO {

    private String name;
    private String description;
    private Double price;
    private String category;
    private String imageUrl;
}
