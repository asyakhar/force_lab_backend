package com.sport.club.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final Map<String, TokenInfo> refreshTokenStorage = new ConcurrentHashMap<>();
    private static final long REFRESH_TOKEN_EXPIRATION_HOURS = 168; // 7 дней

    public String createRefreshToken(String email) {
        String refreshToken = UUID.randomUUID().toString();
        TokenInfo tokenInfo = new TokenInfo(email, LocalDateTime.now().plusHours(REFRESH_TOKEN_EXPIRATION_HOURS));
        refreshTokenStorage.put(refreshToken, tokenInfo);
        return refreshToken;
    }

    public String getEmailByRefreshToken(String token) {
        TokenInfo tokenInfo = refreshTokenStorage.get(token);
        if (tokenInfo == null) {
            return null;
        }

        // Проверка на истечение срока действия
        if (tokenInfo.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenStorage.remove(token);
            return null;
        }

        return tokenInfo.getEmail();
    }

    public void deleteRefreshToken(String token) {
        refreshTokenStorage.remove(token);
    }

    // Вспомогательный класс для хранения информации о токене
    private static class TokenInfo {
        private final String email;
        private final LocalDateTime expiresAt;

        public TokenInfo(String email, LocalDateTime expiresAt) {
            this.email = email;
            this.expiresAt = expiresAt;
        }

        public String getEmail() {
            return email;
        }

        public LocalDateTime getExpiresAt() {
            return expiresAt;
        }
    }
}