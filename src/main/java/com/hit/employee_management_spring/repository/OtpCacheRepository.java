package com.hit.employee_management_spring.repository;

import com.hit.employee_management_spring.domain.cache.OtpCache;
import com.hit.employee_management_spring.domain.cache.OtpTokenCache;

public interface OtpCacheRepository {
    void saveOtp(String email, OtpCache otpCache, long ttlSeconds);
    OtpCache getOtp(String email);
    void deleteOtp(String email);
    void saveToken(String token, OtpTokenCache tokenCache, long ttlSeconds);
    OtpTokenCache getToken(String token);
    void deleteToken(String token);
}
