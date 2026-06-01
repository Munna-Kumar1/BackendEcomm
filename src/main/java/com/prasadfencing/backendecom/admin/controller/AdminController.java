package com.prasadfencing.backendecom.admin.controller;

import com.prasadfencing.backendecom.admin.service.AdminService;
import com.prasadfencing.backendecom.auth.entity.User;
import com.prasadfencing.backendecom.common.pagination.PageRequestDto;
import com.prasadfencing.backendecom.common.pagination.PageResponse;
import com.prasadfencing.backendecom.order.entity.Order;
import com.prasadfencing.backendecom.payment.entity.Payment;
import com.prasadfencing.backendecom.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // USERS
    @GetMapping("/users")
    public ResponseEntity<PageResponse<User>> getUsers(PageRequestDto request) {
        return ResponseEntity.ok(adminService.getAllUsers(request));
    }

    // PRODUCTS
    @GetMapping("/products")
    public ResponseEntity<PageResponse<Product>> getProducts(PageRequestDto request) {
        return ResponseEntity.ok(adminService.getAllProducts(request));
    }

    // ORDERS
    @GetMapping("/orders")
    public ResponseEntity<PageResponse<Order>> getOrders(PageRequestDto request) {
        return ResponseEntity.ok(adminService.getAllOrders(request));
    }

    // PAYMENTS
    @GetMapping("/payments")
    public ResponseEntity<PageResponse<Payment>> getPayments(PageRequestDto request) {
        return ResponseEntity.ok(adminService.getAllPayments(request));
    }
}