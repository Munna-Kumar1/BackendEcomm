package com.prasadfencing.backendecom.billing.entity;

import com.prasadfencing.backendecom.auth.entity.User;
import com.prasadfencing.backendecom.order.entity.Order;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String invoiceNumber;

    private String type; // B2C / B2B

    private Double totalAmount;

    private String status; // GENERATED, PAID

    private String gstNumber;

    private LocalDateTime createdAt;

    @ManyToOne
    private User user;

    @OneToOne
    private Order order;
}