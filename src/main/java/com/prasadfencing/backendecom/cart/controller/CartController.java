package com.prasadfencing.backendecom.cart.controller;

import com.prasadfencing.backendecom.cart.dto.AddToCartRequest;
import com.prasadfencing.backendecom.cart.dto.CartResponse;
import com.prasadfencing.backendecom.cart.entity.CartItem;
import com.prasadfencing.backendecom.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class CartController {

    private final CartService cartService;

    // ADD
    @PostMapping("/add")
    public String addToCart(@RequestBody AddToCartRequest request) {
        return cartService.addToCart(request);
    }

    // GET CART
    @GetMapping
    public List<CartResponse> getMyCart() {
        return cartService.getMyCart();
    }

    // UPDATE
    @PutMapping("/item/{cartItemId}")
    public String updateQuantity(@PathVariable Long cartItemId,
                                 @RequestBody Integer quantity) {
        return cartService.updateQuantity(cartItemId, quantity);
    }

    // REMOVE
    @DeleteMapping("/item/{cartItemId}")
    public String removeItem(@PathVariable Long cartItemId) {
        return cartService.removeItem(cartItemId);
    }
}