package com.prasadfencing.backendecom.order.service;

import com.prasadfencing.backendecom.order.dto.OrderResponseDTO;
import com.prasadfencing.backendecom.order.dto.PlaceOrderRequestDTO;
import com.prasadfencing.backendecom.order.entity.Order;

import java.util.List;

public interface OrderService {

    OrderResponseDTO placeOrder(PlaceOrderRequestDTO request);

    List<OrderResponseDTO> getMyOrders();

    Order getOrderById(Long id);
}