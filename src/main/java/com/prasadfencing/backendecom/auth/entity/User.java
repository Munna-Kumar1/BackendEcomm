package com.prasadfencing.backendecom.auth.entity;

import com.prasadfencing.backendecom.auth.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean verified;

    // OTP
    private String otp;
    private LocalDateTime otpExpiry;

    // 🔐 REFRESH TOKEN FIELDS
    private String refreshToken;
    private LocalDateTime refreshTokenExpiry;
}