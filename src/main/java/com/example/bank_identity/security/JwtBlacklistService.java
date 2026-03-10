package com.example.bank_identity.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class JwtBlacklistService {

    private final Map<String, Long> blacklistedTokens = new ConcurrentHashMap<>();

    public void blacklistToken(String token, long expirationTimeMs) {
        blacklistedTokens.put(token, expirationTimeMs);
        log.info("[Blacklist] Token đã bị thu hồi. Tổng: {}", blacklistedTokens.size());
    }

    public boolean isBlacklisted(String token) {
        return blacklistedTokens.containsKey(token);
    }

    @Scheduled(fixedRate = 3600000)
    public void cleanupExpiredTokens() {
        long now = System.currentTimeMillis();
        int before = blacklistedTokens.size();
        blacklistedTokens.entrySet().removeIf(e -> e.getValue() < now);
        int removed = before - blacklistedTokens.size();
        if (removed > 0) {
            log.info("[Blacklist] Đã xóa {} token hết hạn. Còn lại: {}", removed, blacklistedTokens.size());
        }
    }
}