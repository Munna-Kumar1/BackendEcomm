package com.prasadfencing.backendecom.auth.service;

import com.prasadfencing.backendecom.auth.entity.BlacklistedToken;
import com.prasadfencing.backendecom.auth.repository.BlacklistedTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final BlacklistedTokenRepository repository;

    public void blacklist(String token, LocalDateTime expiryDate) {
        repository.save(
                BlacklistedToken.builder()
                        .token(token)
                        .expiryDate(expiryDate)
                        .build()
        );
    }

    public boolean isBlacklisted(String token) {
        return repository.existsByToken(token);
    }
}