package com.prasadfencing.backendecom.payment.controller;

import com.prasadfencing.backendecom.payment.dto.CreatePaymentRequest;
import com.prasadfencing.backendecom.payment.dto.VerifyPaymentRequest;
import com.prasadfencing.backendecom.payment.entity.Payment;
import com.prasadfencing.backendecom.payment.service.PaymentService;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.Lint;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-order")
    public String createOrder(@RequestBody CreatePaymentRequest request) {
        return paymentService.createOrder(request);
    }

    @PostMapping("/verify")
    public String verifyPayment(@RequestBody VerifyPaymentRequest request) {
        paymentService.verifyPayment(request);
        return "Payment successful";
    }

    @GetMapping
    public List<Payment> getAllPayments() {
        return paymentService.getAllPayments();
    }
}