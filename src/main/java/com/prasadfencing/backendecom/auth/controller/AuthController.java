package com.prasadfencing.backendecom.auth.controller;

import com.prasadfencing.backendecom.auth.dto.request.*;
import com.prasadfencing.backendecom.auth.dto.response.AuthResponse;
import com.prasadfencing.backendecom.auth.service.AuthService;
import com.prasadfencing.backendecom.auth.service.RateLimitService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RateLimitService rateLimitService; // ✅ FIX: was missing

    // 5 attempts per IP per hour
    @PostMapping("/register")
    public ResponseEntity<String> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest) {

        rateLimitService.checkLimit(
                "rate:register:" + httpRequest.getRemoteAddr(), 5, 3600);

        return ResponseEntity.ok(authService.register(request));
    }

    // 5 attempts per IP per 15 minutes
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        rateLimitService.checkLimit(
                "rate:login:" + httpRequest.getRemoteAddr(), 5, 900);

        return ResponseEntity.ok(authService.login(request));
    }

    // ✅ FIX: endpoint was completely missing — 3 attempts per email per 10 min
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request) {

        rateLimitService.checkLimit(
                "rate:verify:" + request.getEmail(), 3, 600);

        return ResponseEntity.ok(authService.verifyOtp(request));
    }

    // ✅ FIX: endpoint was completely missing — 3 resends per email per hour
    @PostMapping("/resend-otp")
    public ResponseEntity<String> resendOtp(
            @RequestParam String email) {

        rateLimitService.checkLimit(
                "rate:resend:" + email, 3, 3600);

        return ResponseEntity.ok(authService.resendOtp(email));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String email
    ) {

        String token = authHeader.substring(7); // remove Bearer

        return ResponseEntity.ok(authService.logout(email, token));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @RequestBody ForgotPasswordRequest request) {

        return ResponseEntity.ok(authService.forgotPassword(request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        return ResponseEntity.ok(authService.resetPassword(request));
    }
}