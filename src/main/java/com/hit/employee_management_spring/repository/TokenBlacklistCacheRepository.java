package com.hit.employee_management_spring.repository;

public interface TokenBlacklistCacheRepository {
    void blacklist(String token, long ttlSeconds);
    boolean isBlacklisted(String token);
}
