package com.prasadfencing.backendecom.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequestDTO {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull
    @Positive(message = "Price must be greater than 0")
    private Double price;

    @NotBlank
    private String category;

    private String imageUrl;
}