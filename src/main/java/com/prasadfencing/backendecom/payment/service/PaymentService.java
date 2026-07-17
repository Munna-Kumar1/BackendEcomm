package com.prasadfencing.backendecom.payment.service;

import com.prasadfencing.backendecom.auth.entity.User;
import com.prasadfencing.backendecom.auth.repository.UserRepository;
import com.prasadfencing.backendecom.billing.service.InvoiceService;
import com.prasadfencing.backendecom.cart.entity.CartItem;
import com.prasadfencing.backendecom.cart.repository.CartRepository;
import com.prasadfencing.backendecom.order.entity.Order;
import com.prasadfencing.backendecom.order.enums.OrderStatus;
import com.prasadfencing.backendecom.order.repository.OrderRepository;
import com.prasadfencing.backendecom.order.service.OrderService;
import com.prasadfencing.backendecom.payment.dto.CreatePaymentRequest;
import com.prasadfencing.backendecom.payment.dto.VerifyPaymentRequest;
import com.prasadfencing.backendecom.payment.entity.Payment;
import com.prasadfencing.backendecom.payment.enums.PaymentStatus;
import com.prasadfencing.backendecom.payment.repository.PaymentRepository;
import com.prasadfencing.backendecom.payment.util.RazorpaySignatureUtil;
import com.razorpay.RazorpayClient;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final InvoiceService invoiceService;

    @Value("${razorpay.key}")
    private String key;

    @Value("${razorpay.secret}")
    private String secret;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public String createOrder(CreatePaymentRequest request) {

        try {
            RazorpayClient client = new RazorpayClient(key, secret);

            Order order = orderService.getOrderById(request.getOrderId());

            JSONObject options = new JSONObject();
            options.put("amount", order.getTotalAmount() * 100);
            options.put("currency", "INR");
            options.put("receipt", "order_" + System.currentTimeMillis());

            com.razorpay.Order razorpayOrder = client.orders.create(options);

            Payment payment = Payment.builder()
                    .razorpayOrderId(razorpayOrder.get("id"))
                    .amount(order.getTotalAmount())
                    .status(PaymentStatus.CREATED)
                    .user(getCurrentUser())
                    .order(order)
                    .createdAt(LocalDateTime.now())
                    .build();

            paymentRepository.save(payment);

            return razorpayOrder.toString();

        } catch (Exception e) {
            throw new RuntimeException("Payment creation failed");
        }
    }

    @Transactional
    public void verifyPayment(VerifyPaymentRequest request) {

        Payment payment = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        boolean valid = RazorpaySignatureUtil.verifySignature(
                request.getRazorpayOrderId(),
                request.getRazorpayPaymentId(),
                request.getRazorpaySignature(),
                secret
        );

        if (!valid) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new RuntimeException("Invalid signature");
        }

        // ✅ PAYMENT SUCCESS
        payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
        payment.setStatus(PaymentStatus.SUCCESS);

        Order order = payment.getOrder();
        order.setStatus(OrderStatus.PAID);

        orderRepository.save(order);
        paymentRepository.save(payment);

        // 🔥 NEW STEP: AUTO INVOICE GENERATION
        invoiceService.generateInvoiceFromOrder(order.getId());
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
    public String getKey() {
        return key;
    }
}