package com.prasadfencing.backendecom.product.controller;

import com.prasadfencing.backendecom.product.dto.ProductRequestDTO;
import com.prasadfencing.backendecom.product.dto.ProductResponseDTO;
import com.prasadfencing.backendecom.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;

    @PostMapping
    public ProductResponseDTO create(@RequestBody ProductRequestDTO dto){
        return service.createProduct(dto);
    }

    @GetMapping("/{id}")
    public ProductResponseDTO getById(@PathVariable Long id){
        return service.getProductById(id);
    }

    @GetMapping
    public List<ProductResponseDTO> getAll(){
        return service.getAllProducts();
    }

    @PutMapping("/{id}")
    public ProductResponseDTO update(@PathVariable Long id, @RequestBody ProductRequestDTO dto){
        return service.updateProduct(id,dto);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id){
        service.deleteProduct(id);
        return "Product delete successfully";
    }

    @GetMapping("/search")
    public List<ProductResponseDTO> search(@RequestParam String name){
        return service.searchByName(name);
    }
}
