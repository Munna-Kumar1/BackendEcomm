package com.prasadfencing.backendecom.auth.service;

import com.prasadfencing.backendecom.auth.dto.request.*;
import com.prasadfencing.backendecom.auth.dto.response.AuthResponse;
import com.prasadfencing.backendecom.auth.entity.User;
import com.prasadfencing.backendecom.auth.enums.Role;
import com.prasadfencing.backendecom.auth.repository.UserRepository;
import com.prasadfencing.backendecom.auth.util.OtpGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final OtpGenerator otpGenerator;
    private final TokenBlacklistService blacklistService;


    // ================= REGISTER =================
    public String register(RegisterRequest request) {

        userRepository.findByEmail(request.getEmail()).ifPresent(existing -> {
            if (existing.isVerified()) {
                throw new RuntimeException("User already exists");
            }
            // ✅ FIX: unverified stale user — delete and allow re-registration
            userRepository.delete(existing);
        });

        String otp = otpGenerator.generateOtp(); // ✅ FIX: use SecureRandom OtpGenerator

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.CUSTOMER)
                .verified(false)                              // ✅ FIX: was incorrectly true
                .otp(otp)
                .otpExpiry(LocalDateTime.now().plusMinutes(10))
                .build();

        userRepository.save(user);
        emailService.sendOtp(request.getEmail(), otp);        // ✅ FIX: was never called

        return "OTP sent to " + request.getEmail() + ". Please verify your email.";
    }

    // ================= VERIFY OTP =================
    // ✅ FIX: method was completely missing
    public String verifyOtp(VerifyOtpRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isVerified()) {
            return "Email already verified. Please login.";
        }

        if (user.getOtp() == null || !user.getOtp().equals(request.getOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        if (user.getOtpExpiry() == null ||
                user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired. Please request a new one.");
        }

        user.setVerified(true);
        user.setOtp(null);
        user.setOtpExpiry(null);

        userRepository.save(user);

        return "Email verified successfully. You can now login.";
    }

    // ================= RESEND OTP =================
    // ✅ FIX: method was completely missing
    public String resendOtp(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isVerified()) {
            return "Email already verified. Please login.";
        }

        String otp = otpGenerator.generateOtp();
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));

        userRepository.save(user);
        emailService.sendOtp(email, otp);

        return "OTP resent to " + email;
    }

    // ================= LOGIN =================
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        // ✅ this check was already correct — keeping it
        if (!user.isVerified()) {
            throw new BadCredentialsException("Email not verified. Please verify your email first.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        String accessToken = jwtService.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        String refreshToken = UUID.randomUUID().toString();
        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiry(LocalDateTime.now().plusDays(7));
        userRepository.save(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // ================= REFRESH =================
    public AuthResponse refreshToken(RefreshRequest request) {

        User user = userRepository.findByRefreshToken(request.getRefreshToken())
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));

        if (user.getRefreshTokenExpiry() == null ||
                user.getRefreshTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BadCredentialsException("Refresh token expired");
        }

        String newAccessToken = jwtService.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(user.getRefreshToken())
                .build();
    }

    // ================= LOGOUT =================
    public String logout(String email, String token) {

        // 1. AUTH REPO (clear refresh token)
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRefreshToken(null);
        user.setRefreshTokenExpiry(null);
        userRepository.save(user);

        // 2. EXTRACT EXPIRY FROM JWT
        LocalDateTime expiry = jwtService.extractExpiration(token);

        // 3. BLACKLIST ACCESS TOKEN
        blacklistService.blacklist(token, expiry);

        return "Logged out successfully";
    }

    public String forgotPassword(ForgotPasswordRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isVerified()) {
            throw new RuntimeException("Email not verified");
        }

        String otp = otpGenerator.generateOtp();

        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));

        userRepository.save(user);

        emailService.sendOtp(user.getEmail(), otp);

        return "OTP sent to email for password reset";
    }
    // =============== reset password =============
    public String resetPassword(ResetPasswordRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // OTP check
        if (user.getOtp() == null || !user.getOtp().equals(request.getOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        // OTP expiry check
        if (user.getOtpExpiry() == null ||
                user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        // update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // clear OTP after success
        user.setOtp(null);
        user.setOtpExpiry(null);

        userRepository.save(user);

        return "Password changed successfully";
    }

}