package com.sport.club.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final long REFRESH_TOKEN_EXPIRATION_DAYS = 7;
    private static final String REDIS_KEY_PREFIX = "refresh_token:";

    public String createRefreshToken(String email) {
        String refreshToken = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(
                REDIS_KEY_PREFIX + refreshToken,
                email,
                Duration.ofDays(REFRESH_TOKEN_EXPIRATION_DAYS)
        );
        return refreshToken;
    }

    public String getEmailByRefreshToken(String token) {
        return (String) redisTemplate.opsForValue().get(REDIS_KEY_PREFIX + token);
    }

    public void deleteRefreshToken(String token) {
        redisTemplate.delete(REDIS_KEY_PREFIX + token);
    }
}