package com.hit.employee_management_spring.repository.impl;

import com.hit.employee_management_spring.domain.cache.OtpCache;
import com.hit.employee_management_spring.domain.cache.OtpTokenCache;
import com.hit.employee_management_spring.repository.OtpCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
@Log4j2
public class RedisOtpCacheRepository implements OtpCacheRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String OTP_PREFIX = "otp:pending:";
    private static final String TOKEN_PREFIX = "otp:token:";

    @Override
    public void saveOtp(String email, OtpCache otpCache, long ttlSeconds) {
        redisTemplate.opsForValue().set(OTP_PREFIX + email, otpCache, ttlSeconds, TimeUnit.SECONDS);
    }

    @Override
    public OtpCache getOtp(String email) {
        return (OtpCache) redisTemplate.opsForValue().get(OTP_PREFIX + email);
    }

    @Override
    public void deleteOtp(String email) {
        redisTemplate.delete(OTP_PREFIX + email);
    }

    @Override
    public void saveToken(String token, OtpTokenCache tokenCache, long ttlSeconds) {
        redisTemplate.opsForValue().set(TOKEN_PREFIX + token, tokenCache, ttlSeconds, TimeUnit.SECONDS);
    }

    @Override
    public OtpTokenCache getToken(String token) {
        return (OtpTokenCache) redisTemplate.opsForValue().get(TOKEN_PREFIX + token);
    }

    @Override
    public void deleteToken(String token) {
        redisTemplate.delete(TOKEN_PREFIX + token);
    }
}
