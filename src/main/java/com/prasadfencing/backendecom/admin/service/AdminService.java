package com.prasadfencing.backendecom.admin.service;

import com.prasadfencing.backendecom.admin.dto.AdminDashboardOverviewDTO;
import com.prasadfencing.backendecom.admin.dto.AdminPaymentsResponseDTO;
import com.prasadfencing.backendecom.auth.entity.User;
import com.prasadfencing.backendecom.auth.repository.UserRepository;
import com.prasadfencing.backendecom.common.pagination.PageRequestDto;
import com.prasadfencing.backendecom.common.pagination.PageResponse;
import com.prasadfencing.backendecom.common.pagination.PaginationUtil;
import com.prasadfencing.backendecom.order.dto.OrderItemResponseDTO;
import com.prasadfencing.backendecom.order.dto.OrderResponseDTO;
import com.prasadfencing.backendecom.order.entity.Order;
import com.prasadfencing.backendecom.order.repository.OrderRepository;
import com.prasadfencing.backendecom.payment.entity.Payment;
import com.prasadfencing.backendecom.payment.repository.PaymentRepository;
import com.prasadfencing.backendecom.product.entity.Product;
import com.prasadfencing.backendecom.product.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    // USERS
    public PageResponse<User> getAllUsers(PageRequestDto request) {
        if ("createdAt".equalsIgnoreCase(request.getSortBy())) {
            request.setSortBy("id");
        }
        Pageable pageable = buildPageable(request);
        Page<User> users = userRepository.findAll(pageable);
        return PaginationUtil.toPage(users);
    }

    // PRODUCTS
    public PageResponse<Product> getAllProducts(PageRequestDto request) {
        Pageable pageable = buildPageable(request);
        Page<Product> products = productRepository.findAll(pageable);
        return PaginationUtil.toPage(products);
    }

    // ORDERS (FIXED COMPILER INFERENCE BOUNDS)
    public PageResponse<OrderResponseDTO> getAllOrders(PageRequestDto request) {
        if ("createdAt".equalsIgnoreCase(request.getSortBy())) {
            request.setSortBy("orderDate");
        }
        Pageable pageable = buildPageable(request);
        Page<Order> ordersPage = orderRepository.findAll(pageable);

        // Transform the entities into flattened DTO profiles safely
        Page<OrderResponseDTO> dtoPage = ordersPage.map(this::convertToOrderResponseDTO);

        return PaginationUtil.toPage(dtoPage);
    }

    // PAYMENTS
    public PageResponse<AdminPaymentsResponseDTO> getAllPayments(PageRequestDto request) {
        // Prevent SQL sorting errors on unmapped creation fields
        if ("createdAt".equalsIgnoreCase(request.getSortBy())) {
            request.setSortBy("id");
        }

        Pageable pageable = buildPageable(request);
        Page<Payment> paymentsPage = paymentRepository.findAll(pageable);

        // Convert entity structures to clear admin DTO structures
        Page<AdminPaymentsResponseDTO> dtoPage = paymentsPage.map(this::convertToAdminPaymentsDTO);

        return PaginationUtil.toPage(dtoPage);
    }

    private AdminPaymentsResponseDTO convertToAdminPaymentsDTO(Payment payment) {
        return AdminPaymentsResponseDTO.builder()
                .id(payment.getId())
                .amount(payment.getAmount())
                .status(payment.getStatus() != null ? payment.getStatus().name() : "SUCCESS")
                .paymentMethod(payment.getPaymentMethod() != null ? payment.getPaymentMethod() : "ONLINE")
                .createdAt(payment.getCreatedAt())
                .orderId(payment.getOrder() != null ? payment.getOrder().getId() : null)
                .customerName(payment.getUser() != null ? payment.getUser().getName() : "Platform Customer")
                .customerEmail(payment.getUser() != null ? payment.getUser().getEmail() : "N/A")
                .build();
    }

    // DECOUPLED EXPLICIT MAPPER BLOCK
    private OrderResponseDTO convertToOrderResponseDTO(Order order) {
        return OrderResponseDTO.builder()
                .orderId(order.getId())
                .status(order.getStatus().name())
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount())
                .addressId(order.getAddress() != null ? order.getAddress().getId() : null)
                .items(
                        order.getItems() == null ? null : order.getItems().stream()
                                .map(item -> OrderItemResponseDTO.builder()
                                        .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                                        .productName(item.getProduct() != null ? item.getProduct().getName() : "Unknown Product")
                                        .quantity(item.getQuantity())
                                        .price(item.getPrice())
                                        .build())
                                .toList()
                )
                .build();
    }

    private Pageable buildPageable(PageRequestDto request) {
        String sortBy = (request.getSortBy() != null) ? request.getSortBy() : "id";
        String sortDir = (request.getSortDir() != null) ? request.getSortDir() : "desc";
        Sort sort = "desc".equalsIgnoreCase(sortDir) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        return PageRequest.of(request.getPage(), request.getSize(), sort);
    }
    public PageResponse<AdminDashboardOverviewDTO> getDashboardOverview(PageRequestDto request) {
        if ("createdAt".equalsIgnoreCase(request.getSortBy())) {
            request.setSortBy("orderDate");
        }
        Pageable pageable = buildPageable(request);
        Page<Order> ordersPage = orderRepository.findAll(pageable);

        // Map rows directly to our all-in-one DTO view layout
        Page<AdminDashboardOverviewDTO> dtoPage = ordersPage.map(this::convertToDashboardDTO);
        return PaginationUtil.toPage(dtoPage);
    }

    private AdminDashboardOverviewDTO convertToDashboardDTO(Order order) {
        return AdminDashboardOverviewDTO.builder()
                .orderId(order.getId())
                .orderDate(order.getOrderDate())
                .orderStatus(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                // Mapping WHO
                .customerName(order.getUser() != null ? order.getUser().getName() : "Walk-in Client")
                .customerEmail(order.getUser() != null ? order.getUser().getEmail() : "N/A")
                // Mapping WHERE
                .shippingName(order.getAddress() != null ? order.getAddress().getFullName() : "N/A")
                .shippingStreet(order.getAddress() != null ? order.getAddress().getStreet() : "N/A")
                .shippingCity(order.getAddress() != null ? order.getAddress().getCity() : "N/A")
                .shippingState(order.getAddress() != null ? order.getAddress().getState() : "N/A")
                .shippingPincode(order.getAddress() != null ? order.getAddress().getPincode() : "N/A")
                // Mapping WHAT
                .items(order.getItems() == null ? null : order.getItems().stream()
                        .map(item -> OrderItemResponseDTO.builder()
                                .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                                .productName(item.getProduct() != null ? item.getProduct().getName() : "Product SKU")
                                .quantity(item.getQuantity())
                                .price(item.getPrice())
                                .build())
                        .toList())
                .build();
    }
}