package com.prasadfencing.backendecom.cart.service;

import com.prasadfencing.backendecom.cart.dto.AddToCartRequest;
import com.prasadfencing.backendecom.cart.dto.CartResponse;
import com.prasadfencing.backendecom.cart.entity.CartItem;
import com.prasadfencing.backendecom.cart.repository.CartRepository;
import com.prasadfencing.backendecom.auth.entity.User;
import com.prasadfencing.backendecom.auth.repository.UserRepository;
import com.prasadfencing.backendecom.product.entity.Product;
import com.prasadfencing.backendecom.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    // GET USER
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ADD TO CART
    public String addToCart(AddToCartRequest request) {

        User user = getCurrentUser();

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem existing = cartRepository
                .findByUserIdAndProductId(user.getId(), request.getProductId())
                .orElse(null);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + request.getQuantity());
            cartRepository.save(existing);
            return "Cart updated";
        }

        CartItem item = CartItem.builder()
                .user(user)
                .product(product)
                .quantity(request.getQuantity())
                .build();

        cartRepository.save(item);

        return "Product added to cart";
    }

    // GET MY CART
    public List<CartResponse> getMyCart() {

        User user = getCurrentUser();

        return cartRepository.findByUser(user)
                .stream()
                .map(item -> CartResponse.builder()
                        .cartId(item.getId())
                        .productName(item.getProduct().getName())
                        .price(item.getProduct().getPrice())
                        .quantity(item.getQuantity())
                        .total(item.getProduct().getPrice() * item.getQuantity())
                        .build()
                )
                .toList();
    }

    // UPDATE QUANTITY
    public String updateQuantity(Long cartId, Integer quantity) {

        User user = getCurrentUser();

        CartItem item = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        // SECURITY CHECK
        if (!item.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not allowed");
        }

        if (quantity <= 0) {
            cartRepository.delete(item);
            return "Item removed";
        }

        item.setQuantity(quantity);
        cartRepository.save(item);

        return "Cart updated";
    }

    // REMOVE ITEM
    public String removeItem(Long cartId) {

        User user = getCurrentUser();

        CartItem item = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        // SECURITY CHECK
        if (!item.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not allowed");
        }

        cartRepository.delete(item);

        return "Item removed from cart";
    }
}