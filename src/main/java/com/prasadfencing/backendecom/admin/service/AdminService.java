package com.prasadfencing.backendecom.admin.service;

import com.prasadfencing.backendecom.auth.entity.User;
import com.prasadfencing.backendecom.auth.repository.UserRepository;
import com.prasadfencing.backendecom.common.pagination.PageRequestDto;
import com.prasadfencing.backendecom.common.pagination.PageResponse;
import com.prasadfencing.backendecom.common.pagination.PaginationUtil;
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

    // ORDERS
    public PageResponse<Order> getAllOrders(PageRequestDto request) {

        Pageable pageable = buildPageable(request);

        Page<Order> orders = orderRepository.findAll(pageable);

        return PaginationUtil.toPage(orders);
    }

    // PAYMENTS
    public PageResponse<Payment> getAllPayments(PageRequestDto request) {

        Pageable pageable = buildPageable(request);

        Page<Payment> payments = paymentRepository.findAll(pageable);

        return PaginationUtil.toPage(payments);
    }

    // COMMON PAGINATION METHOD
    private Pageable buildPageable(PageRequestDto request) {

        Sort sort = "desc".equalsIgnoreCase(request.getSortDir())
                ? Sort.by(request.getSortBy()).descending()
                : Sort.by(request.getSortBy()).ascending();

        return PageRequest.of(
                request.getPage(),
                request.getSize(),
                sort
        );
    }
}
