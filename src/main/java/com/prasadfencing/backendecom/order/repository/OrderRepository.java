package com.prasadfencing.backendecom.order.repository;

import com.prasadfencing.backendecom.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);

    // FIXED: Added EntityGraph to pull user and address relationships eagerly in one single query
    @EntityGraph(attributePaths = {"user", "address", "items"})
    Page<Order> findAll(Pageable pageable);
}