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

    // ADD TO CART
    @PostMapping("/add")
    public String addToCart(@RequestBody AddToCartRequest request) {
        return cartService.addToCart(request);
    }

    // GET CART
    @GetMapping
    public List<CartResponse> getMyCart() {
        return cartService.getMyCart();
    }

    // UPDATE QUANTITY
    @PutMapping("/{cartId}")
    public String updateQuantity(@PathVariable Long cartId,
                                 @RequestBody Integer quantity) {
        return cartService.updateQuantity(cartId, quantity);
    }

    // REMOVE ITEM
    @DeleteMapping("/{cartId}")
    public String removeItem(@PathVariable Long cartId) {
        return cartService.removeItem(cartId);
    }
}