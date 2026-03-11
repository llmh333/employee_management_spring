package com.hit.employee_management_spring.repository.impl;

import com.hit.employee_management_spring.repository.TokenBlacklistCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
@Log4j2
public class RedisTokenBlacklistRepository implements TokenBlacklistCacheRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String KEY_PREFIX = "blacklist:";

    @Override
    public void blacklist(String token, long ttlSeconds) {
        try {
            redisTemplate.opsForValue().set(KEY_PREFIX + token, "true", ttlSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Failed to blacklist token in Redis: {}", e.getMessage(), e);
        }
    }

    @Override
    public boolean isBlacklisted(String token) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(KEY_PREFIX + token));
        } catch (Exception e) {
            log.error("Failed to check blacklist in Redis: {}", e.getMessage(), e);
            return false;
        }
    }
}
