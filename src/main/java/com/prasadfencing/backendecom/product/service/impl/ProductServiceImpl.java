package com.prasadfencing.backendecom.product.service.impl;

import com.prasadfencing.backendecom.exception.custom.ResourceNotFoundException;
import com.prasadfencing.backendecom.product.dto.ProductRequestDTO;
import com.prasadfencing.backendecom.product.dto.ProductResponseDTO;
import com.prasadfencing.backendecom.product.entity.Product;
import com.prasadfencing.backendecom.product.repository.ProductRepository;
import com.prasadfencing.backendecom.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;

    private ProductResponseDTO map(Product p) {
        return ProductResponseDTO.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .category(p.getCategory())
                .imageUrl(p.getImageUrl())
                .isActive(p.getIsActive())
                .createdAt(p.getCreatedAt())
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

        return map(repository.save(product));
    }

    @Override
    public ProductResponseDTO getProductById(Long id) {

        Product product = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found"));

        return map(product);
    }

    @Override
    public List<ProductResponseDTO> getAllProducts() {
        return repository.findAll()
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO dto) {

        Product product = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found"));

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setCategory(dto.getCategory());
        product.setImageUrl(dto.getImageUrl());

        return map(repository.save(product));
    }

    @Override
    public void deleteProduct(Long id) {

        Product product = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found"));

        repository.delete(product);
    }

    @Override
    public List<ProductResponseDTO> searchByName(String name) {
        return repository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::map)
                .toList();
    }
}