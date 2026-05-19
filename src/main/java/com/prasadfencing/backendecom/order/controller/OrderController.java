package com.prasadfencing.backendecom.order.controller;

import com.prasadfencing.backendecom.order.dto.OrderResponseDTO;
import com.prasadfencing.backendecom.order.dto.PlaceOrderRequestDTO;
import com.prasadfencing.backendecom.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/place")
    public OrderResponseDTO placeOrder(@RequestBody PlaceOrderRequestDTO request) {
        return orderService.placeOrder(request);
    }

    @GetMapping
    public List<OrderResponseDTO> getMyOrders() {
        return orderService.getMyOrders();
    }
}