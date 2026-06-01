package com.prasadfencing.backendecom.order.service.impl;

import com.prasadfencing.backendecom.address.entity.Address;
import com.prasadfencing.backendecom.address.repository.AddressRepository;
import com.prasadfencing.backendecom.auth.entity.User;
import com.prasadfencing.backendecom.auth.repository.UserRepository;
import com.prasadfencing.backendecom.cart.entity.CartItem;
import com.prasadfencing.backendecom.cart.repository.CartRepository;
import com.prasadfencing.backendecom.delivery.repository.ServiceablePincodeRepository;
import com.prasadfencing.backendecom.order.dto.OrderItemResponseDTO;
import com.prasadfencing.backendecom.order.dto.OrderResponseDTO;
import com.prasadfencing.backendecom.order.dto.PlaceOrderRequestDTO;
import com.prasadfencing.backendecom.order.entity.Order;
import com.prasadfencing.backendecom.order.entity.OrderItem;
import com.prasadfencing.backendecom.order.enums.OrderStatus;
import com.prasadfencing.backendecom.order.repository.OrderRepository;
import com.prasadfencing.backendecom.order.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final ServiceablePincodeRepository pincodeRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ---------------- PLACE ORDER ----------------
    @Override
    @Transactional
    public OrderResponseDTO placeOrder(PlaceOrderRequestDTO request) {

        User user = getCurrentUser();

        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!pincodeRepository.existsByPincode(address.getPincode())) {
            throw new RuntimeException("Delivery not available");
        }

        List<CartItem> cartItems = cartRepository.findByUser(user);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING_PAYMENT);

        List<OrderItem> items = new ArrayList<>();
        double total = 0;

        for (CartItem item : cartItems) {

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(item.getProduct());
            oi.setQuantity(item.getQuantity());
            oi.setPrice(item.getProduct().getPrice());

            total += item.getProduct().getPrice() * item.getQuantity();
            items.add(oi);
        }

        order.setItems(items);
        order.setTotalAmount(total);

        cartRepository.deleteAll(cartItems);

        Order saved = orderRepository.save(order);

        return mapToDTO(saved);
    }

    // ---------------- GET MY ORDERS ----------------
    @Override
    public List<OrderResponseDTO> getMyOrders() {

        User user = getCurrentUser();

        return orderRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    // ---------------- GET ORDER BY ID ----------------
    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    // ---------------- MAPPER ----------------
    private OrderResponseDTO mapToDTO(Order order) {

        return OrderResponseDTO.builder()
                .orderId(order.getId())
                .status(order.getStatus().name())
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount())
                .addressId(order.getAddress().getId())
                .items(
                        order.getItems().stream()
                                .map(item -> OrderItemResponseDTO.builder()
                                        .productId(item.getProduct().getId())
                                        .productName(item.getProduct().getName())
                                        .quantity(item.getQuantity())
                                        .price(item.getPrice())
                                        .build())
                                .toList()
                )
                .build();
    }
}