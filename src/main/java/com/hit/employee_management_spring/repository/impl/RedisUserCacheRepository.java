package com.hit.employee_management_spring.repository.impl;

import com.hit.employee_management_spring.domain.entity.User;
import com.hit.employee_management_spring.repository.UserCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Repository
@Log4j2
public class RedisUserCacheRepository implements UserCacheRepository {

    private final RedisTemplate redisTemplate;
    private static final String KEY_PREFIX = "user_info:";

    @Override
    public boolean save(User user) {
        try {
            String key = KEY_PREFIX + user.getUsername();
            redisTemplate.opsForValue().set(key, user, 30, TimeUnit.MINUTES);
            return true;
        } catch (Exception e) {
            log.error("Failed to save user to Redis cache: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public User get(String username) {
        String key = KEY_PREFIX + username;
        return (User) redisTemplate.opsForValue().get(key);
    }

    @Override
    public boolean delete(String username) {
        String key = KEY_PREFIX + username;
        redisTemplate.delete(key);
        return true;
    }
}
