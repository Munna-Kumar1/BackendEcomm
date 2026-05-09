package com.prasadfencing.backendecom.product.service;


import com.prasadfencing.backendecom.product.dto.ProductRequestDTO;
import com.prasadfencing.backendecom.product.dto.ProductResponseDTO;

import java.util.List;

public interface ProductService {

    ProductResponseDTO createProduct(ProductRequestDTO dto);

    ProductResponseDTO getProductById(Long id);

    List<ProductResponseDTO> getAllProducts();

    ProductResponseDTO updateProduct(Long id, ProductRequestDTO dto);

    void deleteProduct(Long id);

    List<ProductResponseDTO> searchByName(String name);
}
