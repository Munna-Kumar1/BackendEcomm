package com.prasadfencing.backendecom.admin.controller;

import com.prasadfencing.backendecom.auth.repository.UserRepository;
import com.prasadfencing.backendecom.order.entity.Order;
import com.prasadfencing.backendecom.order.enums.OrderStatus;
import com.prasadfencing.backendecom.order.repository.OrderRepository;
import com.prasadfencing.backendecom.payment.repository.PaymentRepository;
import com.prasadfencing.backendecom.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    // USERS
    @GetMapping("/users")
    public List<?> getAllUsers() {
        return userRepository.findAll();
    }

    // PRODUCTS
    @GetMapping("/products")
    public List<?> getAllProducts() {
        return productRepository.findAll();
    }

    // ORDERS
    @GetMapping("/orders")
    public List<?> getAllOrders() {
        return orderRepository.findAll();
    }

    // PAYMENTS
    @GetMapping("/payments")
    public List<?> getAllPayments() {
        return paymentRepository.findAll();
    }

    // UPDATE ORDER STATUS
    @PutMapping("/orders/{id}/status")
    public String updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
        order.setStatus(orderStatus);

        orderRepository.save(order);

        return "Order status updated successfully";
    }
}