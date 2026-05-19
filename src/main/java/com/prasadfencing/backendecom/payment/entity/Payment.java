package com.prasadfencing.backendecom.payment.entity;

import com.prasadfencing.backendecom.auth.entity.User;
import com.prasadfencing.backendecom.order.entity.Order;
import com.prasadfencing.backendecom.payment.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String razorpayOrderId;

    private String razorpayPaymentId;

    private String razorpaySignature;

    private Double amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String paymentMethod;

    private LocalDateTime createdAt;

    @ManyToOne
    private User user;

    @OneToOne
    private Order order;
}