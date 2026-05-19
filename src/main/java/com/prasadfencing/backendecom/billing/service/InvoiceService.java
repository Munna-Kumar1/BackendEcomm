package com.prasadfencing.backendecom.billing.service;

import com.prasadfencing.backendecom.auth.entity.User;
import com.prasadfencing.backendecom.auth.repository.UserRepository;
import com.prasadfencing.backendecom.billing.entity.Invoice;
import com.prasadfencing.backendecom.billing.repository.InvoiceRepository;
import com.prasadfencing.backendecom.billing.util.InvoiceNumberGenerator;
import com.prasadfencing.backendecom.order.entity.Order;
import com.prasadfencing.backendecom.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ADMIN: B2C
    public Invoice generateInvoiceFromOrder(Long orderId) {

        User user = getCurrentUser();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not allowed");
        }

        Invoice invoice = Invoice.builder()
                .invoiceNumber(InvoiceNumberGenerator.generate())
                .type("B2C")
                .order(order)
                .user(user)
                .totalAmount(order.getTotalAmount())
                .status("GENERATED")
                .createdAt(LocalDateTime.now())
                .build();

        return invoiceRepository.save(invoice);
    }

    // ADMIN: B2B
    public Invoice createB2BInvoice(Double amount, String gstNumber) {

        User user = getCurrentUser();

        Invoice invoice = Invoice.builder()
                .invoiceNumber(InvoiceNumberGenerator.generate())
                .type("B2B")
                .user(user)
                .totalAmount(amount)
                .gstNumber(gstNumber)
                .status("GENERATED")
                .createdAt(LocalDateTime.now())
                .build();

        return invoiceRepository.save(invoice);
    }

    // USER
    public List<Invoice> getMyInvoices() {
        return invoiceRepository.findByUserId(getCurrentUser().getId());
    }

    // ADMIN
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public Invoice getById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
    }
}