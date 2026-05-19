package com.prasadfencing.backendecom.auth.service;

import com.prasadfencing.backendecom.exception.custom.TooManyRequestsException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final RedisTemplate<String, String> redisTemplate;

    public void checkLimit(String key, int limit, long ttlSeconds) {

        Long count = redisTemplate.opsForValue().increment(key);

        if (count != null && count == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(ttlSeconds));
        }

        if (count != null && count > limit) {
            throw new TooManyRequestsException(); // ✅ FIX: typed exception
        }
    }
}