package com.prasadfencing.backendecom.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prasadfencing.backendecom.billing.service.InvoiceService;
import com.prasadfencing.backendecom.order.entity.Order;
import com.prasadfencing.backendecom.order.enums.OrderStatus;
import com.prasadfencing.backendecom.order.repository.OrderRepository;
import com.prasadfencing.backendecom.payment.entity.Payment;
import com.prasadfencing.backendecom.payment.enums.PaymentStatus;
import com.prasadfencing.backendecom.payment.repository.PaymentRepository;
import com.prasadfencing.backendecom.payment.util.RazorpaySignatureUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentWebhookService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final InvoiceService invoiceService;

    @Value("${razorpay.secret}")
    private String secret;

    public void handleWebhook(String payload, String signature) {

        try {

            // =========================
            // VERIFY SIGNATURE
            // =========================
            boolean valid =
                    RazorpaySignatureUtil.verifyWebhookSignature(
                            payload,
                            signature,
                            secret
                    );

            if (!valid) {
                throw new RuntimeException(
                        "Invalid webhook signature"
                );
            }

            // =========================
            // PARSE JSON
            // =========================
            ObjectMapper mapper = new ObjectMapper();

            Map<String, Object> map =
                    mapper.readValue(payload, Map.class);

            Map<String, Object> payloadMap =
                    (Map<String, Object>) map.get("payload");

            Map<String, Object> paymentMap =
                    (Map<String, Object>) payloadMap.get("payment");

            Map<String, Object> entity =
                    (Map<String, Object>) paymentMap.get("entity");

            String razorpayOrderId =
                    (String) entity.get("order_id");

            String razorpayPaymentId =
                    (String) entity.get("id");

            // =========================
            // FIND PAYMENT
            // =========================
            Payment payment = paymentRepository
                    .findByRazorpayOrderId(razorpayOrderId)
                    .orElseThrow(() ->
                            new RuntimeException("Payment not found"));

            // =========================
            // PREVENT DUPLICATE PROCESSING
            // =========================
            if (payment.getStatus() == PaymentStatus.SUCCESS) {
                return;
            }

            // =========================
            // UPDATE PAYMENT
            // =========================
            payment.setRazorpayPaymentId(
                    razorpayPaymentId
            );

            payment.setStatus(
                    PaymentStatus.SUCCESS
            );

            paymentRepository.save(payment);

            // =========================
            // UPDATE ORDER
            // =========================
            Order order = payment.getOrder();

            order.setStatus(OrderStatus.PAID);

            orderRepository.save(order);

            // =========================
            // GENERATE INVOICE
            // =========================
            invoiceService.generateInvoiceFromOrder(
                    order.getId()
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Webhook failed: " + e.getMessage()
            );
        }
    }
}