package com.prasadfencing.backendecom.billing.service;

import com.prasadfencing.backendecom.auth.entity.User;
import com.prasadfencing.backendecom.auth.repository.UserRepository;
import com.prasadfencing.backendecom.billing.entity.Invoice;
import com.prasadfencing.backendecom.billing.repository.InvoiceRepository;
import com.prasadfencing.backendecom.billing.util.InvoiceNumberGenerator;
import com.prasadfencing.backendecom.order.entity.Order;
import com.prasadfencing.backendecom.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
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
    private final EmailService2 emailService2;

    private final PdfInvoiceService pdfInvoiceService;

    // 🔐 get logged-in user
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Invoice generateInvoiceFromOrder(Long orderId) {

        User user = getCurrentUser();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not allowed");
        }

        if (invoiceRepository.existsByOrderId(orderId)) {
            throw new RuntimeException("Invoice already exists");
        }

        // 1. CREATE INVOICE
        Invoice invoice = Invoice.builder()
                .invoiceNumber(InvoiceNumberGenerator.generate())
                .type("ONLINE")
                .order(order)
                .user(user)
                .totalAmount(order.getTotalAmount())
                .status("GENERATED")
                .createdAt(LocalDateTime.now())
                .build();

        Invoice savedInvoice = invoiceRepository.save(invoice);

        // 2. GENERATE PDF
        byte[] pdf = pdfInvoiceService.generatePdf(savedInvoice.getId());

        // 3. SEND EMAIL
        sendInvoiceEmail(savedInvoice, pdf);

        return savedInvoice;
    }
    // 👤 USER invoices
    public List<Invoice> getMyInvoices() {
        return invoiceRepository.findByUserId(getCurrentUser().getId());
    }

    // 🔎 GET invoice
    public Invoice getById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
    }
    private void sendInvoiceEmail(Invoice invoice, byte[] pdf) {

        String email = invoice.getUser().getEmail();

        String subject = "Invoice " + invoice.getInvoiceNumber();

        String body = "Dear Customer,\n\n"
                + "Your order has been confirmed.\n"
                + "Please find attached invoice.\n\n"
                + "Thank you for shopping with us.";

        emailService2.sendInvoiceEmail(email, subject, body, pdf);
    }
}