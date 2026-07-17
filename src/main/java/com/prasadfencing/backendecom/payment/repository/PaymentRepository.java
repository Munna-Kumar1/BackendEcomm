package com.prasadfencing.backendecom.payment.repository;

import com.prasadfencing.backendecom.payment.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);

    // REMOVED @EntityGraph to stop the 500 Cartesian Join crash
    Page<Payment> findAll(Pageable pageable);
}