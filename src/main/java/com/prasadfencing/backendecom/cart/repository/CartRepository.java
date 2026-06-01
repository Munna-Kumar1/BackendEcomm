package com.prasadfencing.backendecom.cart.repository;

import com.prasadfencing.backendecom.cart.entity.CartItem;
import com.prasadfencing.backendecom.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUser(User user);

    Optional<CartItem> findByUserIdAndProductId(Long userId, Long productId);

    void deleteByUser(User user); // 🔥 optional for checkout clear cart
}