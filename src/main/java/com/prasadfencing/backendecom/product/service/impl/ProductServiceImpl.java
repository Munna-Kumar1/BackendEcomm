package com.prasadfencing.backendecom.product.service.impl;

import com.prasadfencing.backendecom.product.dto.ProductRequestDTO;
import com.prasadfencing.backendecom.product.dto.ProductResponseDTO;
import com.prasadfencing.backendecom.product.entity.Product;
import com.prasadfencing.backendecom.product.repository.ProductRepository;
import com.prasadfencing.backendecom.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;

    private ProductResponseDTO mapToDTO(Product product){
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(product.getCategory())
                .imageUrl(product.getImageUrl())
                .isActive(product.getIsActive())
                .createdAt(product.getCreatedAt())
                .build();
    }

    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO dto) {

        Product product = Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .category(dto.getCategory())
                .imageUrl(dto.getImageUrl())
                .isActive(true)
                .build();
        return mapToDTO(repository.save(product));
    }

    @Override
    public ProductResponseDTO getProductById(Long id) {
        Product product = repository.findById(id)
                .orElseThrow(()->new RuntimeException("Product not found"));
        return mapToDTO(product);
    }

    @Override
    public List<ProductResponseDTO> getAllProducts() {
        return repository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO dto) {

        Product product = repository.findById(id)
                .orElseThrow(()->new RuntimeException("Product not found"));

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setCategory(dto.getCategory());
        product.setImageUrl(dto.getImageUrl());
        return mapToDTO(repository.save(product));
    }

    @Override
    public void deleteProduct(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<ProductResponseDTO> searchByName(String name) {
        return repository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
}
