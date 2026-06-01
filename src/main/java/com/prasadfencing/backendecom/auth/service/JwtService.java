package com.prasadfencing.backendecom.auth.service;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    // ======================
    // GENERATE TOKEN
    // ======================
    public String generateToken(String email, String role) {

        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    // ======================
    // EXTRACT EMAIL
    // ======================
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    // ======================
    // VALIDATE TOKEN (FIX ADDED)
    // ======================
    public boolean isTokenValid(String token) {
        return extractEmail(token) != null && !isTokenExpired(token);
    }

    // ======================
    // CHECK EXPIRATION
    // ======================
    private boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    // ======================
    // CLAIMS
    // ======================
    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }
    public LocalDateTime extractExpiration(String token) {

        Date expiry = extractAllClaims(token).getExpiration();

        return expiry.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}